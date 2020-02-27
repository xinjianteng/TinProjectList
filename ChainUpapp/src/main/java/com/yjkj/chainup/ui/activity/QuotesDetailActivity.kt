package com.yjkj.chainup.ui.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.PopupWindow
import android.widget.TextView
import com.fengniao.news.util.JsonUtils
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.yjkj.chainup.R
import com.yjkj.chainup.base.BaseActivity
import com.yjkj.chainup.bean.NewTransactionData
import com.yjkj.chainup.bean.QuotesData
import com.yjkj.chainup.bean.SymbolData
import com.yjkj.chainup.bean.TransactionData
import com.yjkj.chainup.klinechart.CoupleChartGestureListener
import com.yjkj.chainup.klinechart.MyCombinedChart
import com.yjkj.chainup.klinechart.XMarkerView
import com.yjkj.chainup.klinechart.YMarkerView
import com.yjkj.chainup.klinechart.bean.KLineBean
import com.yjkj.chainup.klinechart.bean.KLineDataPares
import com.yjkj.chainup.klinechart.bean.KMAEntity
import com.yjkj.chainup.klinechart.bean.VMAEntity
import com.yjkj.chainup.manager.LoginManager
import com.yjkj.chainup.net.api.ApiConstants
import com.yjkj.chainup.ui.fragment.TransactionDetailFragment.Companion.TYPE_BUY
import com.yjkj.chainup.ui.fragment.TransactionDetailFragment.Companion.TYPE_SELL
import com.yjkj.chainup.util.BigDecimalUtils
import com.yjkj.chainup.util.GZIPUtils
import com.yjkj.chainup.util.SymbolInterceptUtils
import com.yjkj.chainup.util.UIUtils
import kotlinx.android.synthetic.main.activity_quotes_detail.*
import kotlinx.android.synthetic.main.item_quotes_deal.view.*
import kotlinx.android.synthetic.main.item_transaction_detail.view.*
import kotlinx.android.synthetic.main.pop_quotes_times.view.*
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import org.json.JSONObject
import java.lang.Exception
import java.math.BigDecimal
import java.net.URI
import java.nio.ByteBuffer
import java.util.*

class QuotesDetailActivity : BaseActivity() {
    //[1min/5min/15min/30min/60min/1day/1week/1month]
    var time: String = "30min"

    var symbol = "ethbtc"

    private var sellViewList = mutableListOf<View>()

    private var buyViewList = mutableListOf<View>()

    private var transactionViewList = mutableListOf<View>()

    private var newTransactions = mutableListOf<NewTransactionData.NewTransactionDetail>()

    private var tabPosition = 0


    /**
     * 用于订阅实时成交信息
     */
    private var newChannel = ""

    //用于订阅深度盘口
    private var step = "0"
    private var detailChannel = ""

    //用于订阅k线行情
    private var kLineChannel = ""

    //用于订阅近24小时行情
    private var oneDayQuotesChannel = ""


    fun getTransactionSendMsg(isSub: Boolean = true): String {
        val name = symbol
        val event = if (isSub)
            "sub"
        else
            "unsub"
        detailChannel = "market_${name}_depth_step${step}"
        return "{\"event\":\"$event\",\"params\":{\"channel\":\"$detailChannel\",\"cb_id\":\"自定义\",\"asks\":150,\"bids\":150}}"
    }

    fun getNewSendMsg(isSub: Boolean = true): String {
        val event = if (isSub)
            "sub"
        else
            "unsub"
        newChannel = "market_${symbol}_trade_ticker"
        return "{\"event\":\"${event}\",\"params\":{\"channel\":\"$newChannel\",\"cb_id\":\"自定义\"}}"
    }

    //订阅历史订单
    fun getHistoryNewSendMsg(): String {
        newChannel = "market_${symbol}_trade_ticker"
        return "{\"event\":\"req\",\"params\":{\"channel\":\"$newChannel\",\"cb_id\":\"自定义\"}}"
    }

    //订阅k线历史数据
    fun getHistorySendMsg(date: String): String {
        return "{\"event\":\"req\",\"params\":{\"channel\":\"market_${symbol}_kline_$date\",\"cb_id\":\"自定义\"}}"
    }

    fun getKLineQuotesSendMsg(time: String, isSub: Boolean = true): String {
        val event = if (isSub)
            "sub"
        else
            "unsub"
        kLineChannel = "market_${symbol}_kline_$time"
        return "{\"event\":\"$event\",\"params\":{\"channel\":\"$kLineChannel\",\"cb_id\":\"自定义\"}}"
    }

    fun getOneDayQuotesSendMsg(isSub: Boolean = true): String {
        val event = if (isSub)
            "sub"
        else
            "unsub"
        oneDayQuotesChannel = "market_${symbol}_ticker"
        return "{\"event\":\"$event\",\"params\":{\"channel\":\"$oneDayQuotesChannel\",\"cb_id\":\"自定义\"}}"
    }


    //解析数据
    private var kLineDataPares: KLineDataPares? = null


    private var timePop: PopupWindow? = null


    private val handler = Handler(Handler.Callback { msg ->
        if (msg.what == 0) {
            val json = JSONObject(msg.obj as String)
            kLineDataPares!!.clearData()
            kLineDataPares!!.parseKLineData(json)
            kLineDataPares!!.handleKLineData(kLineDataPares!!.getkLineDatas())
            setValueFormatter(kLineDataPares!!.getkLineDatas())
            refreshKlineView(tabPosition, kLineDataPares!!)
            refreshVolumeChart(kLineDataPares)
            sendMessage(getKLineQuotesSendMsg(time))

        }
        //最新成交
        if (msg.what == 1) {
            val newTransactionData: NewTransactionData? = JsonUtils.jsonToBean(msg.obj as String, NewTransactionData::class.java)
            if (newTransactionData != null) {
                refreshNewView(newTransactionData.tick.data)
            }
        }
        //成交历史
        if (msg.what == 2) {
            val transactionData = JsonUtils.jsonToBean(msg.obj as String, TransactionData::class.java)
//            transactionData.tick.asks.sortByDescending { it.get(0).asFloat }
            /**
             * 卖盘取最小，买盘取最大
             * 服务器已做过处理，买卖都取前5个即可
             */
            transactionData.tick.asks.sortByDescending { it.get(0).asFloat }
            transactionData.tick.buys.sortByDescending { it.get(0).asFloat }
            refreshDetailView(transactionData)
        }

        //k线行情
        if (msg.what == 3) {
            val kLineData = KLineBean()
            val jsonObject = JSONObject(msg.obj as String)
            val data = jsonObject.optJSONObject("tick") ?: return@Callback true
            kLineData.date = data.optString("id") + "000"
            kLineData.open = BigDecimal(data.optString("open")).toFloat()
            kLineData.close = BigDecimal(data.optString("close")).toFloat()
            kLineData.high = BigDecimal(data.optString("high")).toFloat()
            kLineData.low = BigDecimal(data.optString("low")).toFloat()
            kLineData.vol = BigDecimal(data.optString("vol")).toFloat()

            addData(this.kLineDataPares!!, kLineDataPares!!.getkLineDatas(), kLineData)

            kline_chart.data.notifyDataChanged()
            kline_chart.notifyDataSetChanged()
            kline_chart.invalidate()
            vol_chart.data.notifyDataChanged()
            vol_chart.notifyDataSetChanged()
            vol_chart.invalidate()
        }

        //24小时行情
        if (msg.what == 4) {
            val jsonObject = JSONObject(msg.obj as String)
            val data = jsonObject.optJSONObject("tick") ?: return@Callback true
            val tick = JsonUtils.jsonToBean(data.toString(), QuotesData.Tick::class.java)
            if (TextUtils.isEmpty(tick.vol)) return@Callback true
            tick.rose = Math.round(tick.rose * 10000) / 100.toFloat()
            tv_quotes_change_day.text = tick.rose.toString() + "%"
            tv_high_day.text = SymbolInterceptUtils.interceptData(tick.high, symbol, "price")
            tv_deal_quantity_day.text = SymbolInterceptUtils.interceptData(tick.vol, symbol, "volume")
            tv_low_day.text = SymbolInterceptUtils.interceptData(tick.low, symbol, "price")
            tv_quotes_day.text = SymbolInterceptUtils.interceptData(tick.close, symbol, "price")
            if (tick.rose < 0) {
                tv_quotes_day.setTextColor(ContextCompat.getColor(this@QuotesDetailActivity, R.color.colorDrop))
                tv_quotes_change_day.setTextColor(ContextCompat.getColor(this@QuotesDetailActivity, R.color.colorDrop))
                iv_quotes_day.setImageResource(R.drawable.ic_quotes_drop)
            } else {
                tv_quotes_day.setTextColor(ContextCompat.getColor(this@QuotesDetailActivity, R.color.colorRise))
                tv_quotes_change_day.setTextColor(ContextCompat.getColor(this@QuotesDetailActivity, R.color.colorRise))
                iv_quotes_day.setImageResource(R.drawable.ic_quotes_rise)
            }
        }
        if (msg.what == 5) {
            val datas = JsonUtils.jsonToList(msg.obj.toString(), NewTransactionData.NewTransactionDetail::class.java)
            Collections.sort(datas, { o1, o2 ->
                BigDecimalUtils.sub(o1.ts, o2.ts).toInt()
            })
            refreshNewView(datas)
            sendMessage(getNewSendMsg())
        }
        true
    })


    private var runnable: Runnable? = null

    private var chartType = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quotes_detail)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        symbol = intent.getStringExtra("symbol")
        val name = intent.getStringExtra("name")
        title = name
        initView()

        initChart(kline_chart)
        initChart(vol_chart)
        vol_chart.xAxis.setDrawLabels(false)
        vol_chart.axisRight.setLabelCount(3, false)
        vol_chart.legend.isEnabled = false
        vol_chart.legend.isWordWrapEnabled = false

        initListener()

        initCharData()

        initDetailView()
    }

    private fun initView() {
        tv_minute1.text = "1" + getString(R.string.minute)
        tv_minute5.text = "5" + getString(R.string.minute)
        tv_minute15.text = "15" + getString(R.string.minute)
        tv_minute30.text = "30" + getString(R.string.minute)
        chooseTab(100)
        tabPosition = 5
        findViewById<TextView>(R.id.title).setOnClickListener {
            val intent = Intent(this, ChooseSymbolActivity::class.java)
            startActivityForResult(intent, ChooseSymbolActivity.CODE_CHOOSE_SYMBOL)
        }
        /**
         * 买入
         */
        tv_buy.setOnClickListener {
//            if (!LoginManager.checkLogin(this, true)) return@setOnClickListener
            val intent = Intent()
            intent.putExtra("type", TYPE_BUY)
            intent.putExtra("key", symbol)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

        /**
         * 卖出
         */
        tv_sell.setOnClickListener {
//            if (!LoginManager.checkLogin(this, true)) return@setOnClickListener
            val intent: Intent = Intent()
            intent.putExtra("type", TYPE_SELL)
            intent.putExtra("key", symbol)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
        tv_minute.setOnClickListener {
            chooseTab(1)
        }
        tv_minute1.setOnClickListener {
            chooseTab(2)
        }
        tv_minute5.setOnClickListener {
            chooseTab(3)
        }
        tv_minute15.setOnClickListener {
            chooseTab(4)
        }
        tv_minute30.setOnClickListener {
            chooseTab(5)
        }
        tv_minute60.setOnClickListener {
            chooseTab(6)
        }
        tv_day.setOnClickListener {
            chooseTab(7)
        }
    }

    private fun chooseTab(position: Int) {
        if (tabPosition == position) return
        tv_minute.setTextColor(ContextCompat.getColor(this, R.color.colorTextQuotesDetailTab))
        line_minute.visibility = View.INVISIBLE
        tv_minute1.setTextColor(ContextCompat.getColor(this, R.color.colorTextQuotesDetailTab))
        line_minute1.visibility = View.INVISIBLE
        tv_minute5.setTextColor(ContextCompat.getColor(this, R.color.colorTextQuotesDetailTab))
        line_minute5.visibility = View.INVISIBLE
        tv_minute15.setTextColor(ContextCompat.getColor(this, R.color.colorTextQuotesDetailTab))
        line_minute15.visibility = View.INVISIBLE
        tv_minute30.setTextColor(ContextCompat.getColor(this, R.color.colorTextQuotesDetailTab))
        line_minute30.visibility = View.INVISIBLE
        tv_minute60.setTextColor(ContextCompat.getColor(this, R.color.colorTextQuotesDetailTab))
        line_minute60.visibility = View.INVISIBLE
        tv_day.setTextColor(ContextCompat.getColor(this, R.color.colorTextQuotesDetailTab))
        line_day.visibility = View.INVISIBLE
        when (position) {
            1 -> {
                tv_minute.setTextColor(ContextCompat.getColor(this, R.color.colorAccent))
                line_minute.visibility = View.VISIBLE
                sendMessage(getKLineQuotesSendMsg(time, false))
                time = "1min"
                sendMessage(getHistorySendMsg(time))
                if (!mSocketClient.isOpen) {
                    mSocketClient.close()
                    getData()
                }
                if (tabPosition == 2) {
                    refreshKlineView(1, kLineDataPares!!)
                    refreshVolumeChart(kLineDataPares)
                }
            }
            2 -> {
                tv_minute1.setTextColor(ContextCompat.getColor(this, R.color.colorAccent))
                line_minute1.visibility = View.VISIBLE
                sendMessage(getKLineQuotesSendMsg(time, false))
                time = "1min"
                sendMessage(getHistorySendMsg(time))
                if (!mSocketClient.isOpen) getData()
                if (tabPosition == 1) {
                    refreshKlineView(2, kLineDataPares!!)
                    refreshVolumeChart(kLineDataPares)
                }
            }
            3 -> {
                tv_minute5.setTextColor(ContextCompat.getColor(this, R.color.colorAccent))
                line_minute5.visibility = View.VISIBLE
                if (time != "5min") {
                    sendMessage(getKLineQuotesSendMsg(time, false))
                    time = "5min"
                    sendMessage(getHistorySendMsg(time))
                }
                if (!mSocketClient.isOpen) getData()
            }
            4 -> {
                tv_minute15.setTextColor(ContextCompat.getColor(this, R.color.colorAccent))
                line_minute15.visibility = View.VISIBLE
                if (time != "15min") {
                    sendMessage(getKLineQuotesSendMsg(time, false))
                    time = "15min"
                    sendMessage(getHistorySendMsg(time))
                }
                if (!mSocketClient.isOpen) getData()
            }
            5 -> {
                tv_minute30.setTextColor(ContextCompat.getColor(this, R.color.colorAccent))
                line_minute30.visibility = View.VISIBLE
                if (time != "30min") {
                    sendMessage(getKLineQuotesSendMsg(time, false))
                    time = "30min"
                    sendMessage(getHistorySendMsg(time))
                }
                if (!mSocketClient.isOpen) getData()
            }
            6 -> {
                tv_minute60.setTextColor(ContextCompat.getColor(this, R.color.colorAccent))
                line_minute60.visibility = View.VISIBLE
                if (time != "60min") {
                    sendMessage(getKLineQuotesSendMsg(time, false))
                    time = "60min"
                    sendMessage(getHistorySendMsg(time))
                }
                if (!mSocketClient.isOpen) getData()
            }
            7 -> {
                tv_day.setTextColor(ContextCompat.getColor(this, R.color.colorAccent))
                line_day.visibility = View.VISIBLE
                if (time != "1day") {
                    sendMessage(getKLineQuotesSendMsg(time, false))
                    time = "1day"
                    sendMessage(getHistorySendMsg(time))
                }
                if (!mSocketClient.isOpen) getData()
            }
            else -> {
                tv_minute30.setTextColor(ContextCompat.getColor(this, R.color.colorAccent))
                line_minute30.visibility = View.VISIBLE
            }
        }
        tabPosition = position
    }


    private fun refreshKlineView(position: Int, priceData: KLineDataPares) {
        if (priceData.getkLineDatas().isEmpty()) return
        setKlineMarkerView(priceData)
        if (position == 1) {
            setMinuteChartData(priceData)
        } else {
            setKLineData(priceData)
        }
        kline_chart.notifyDataSetChanged()
        kline_chart.invalidate()
        kline_chart.moveViewToX((priceData.getkLineDatas().size - 1).toFloat())
    }

    private fun refreshVolumeChart(volumeData: KLineDataPares?) {
        if (volumeData != null) {
            if (volumeData.getkLineDatas().isEmpty()) return
            vol_chart.clear()
            setVolMarkerView(volumeData)
            setVolData(volumeData)
            vol_chart.data.notifyDataChanged()
            vol_chart.notifyDataSetChanged()
            vol_chart.invalidate()
            vol_chart.moveViewToX((volumeData.getkLineDatas()!!.size - 1).toFloat())
        }
    }


    private fun resetAllData() {
        //取消订阅
        sendMessage(getNewSendMsg(false))
        sendMessage(getTransactionSendMsg(false))
        sendMessage(getOneDayQuotesSendMsg(false))
        sendMessage(getKLineQuotesSendMsg(time, false))
        //清除数据
        kLineDataPares!!.clearData()
    }


    //初始化选择时间的popwindow
    private fun initPop() {
        timePop = PopupWindow(this)
        timePop!!.width = WindowManager.LayoutParams.WRAP_CONTENT
        timePop!!.height = WindowManager.LayoutParams.WRAP_CONTENT
        timePop!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        timePop!!.isOutsideTouchable = true
        val view = layoutInflater.inflate(R.layout.pop_quotes_times, null)
        //[1min/5min/15min/30min/60min/1day/1week/1month]
        view.tv_time1.setOnClickListener {
            time = "1min"
            UIUtils.showToast(time)
            timePop!!.dismiss()
        }
        view.tv_time5.setOnClickListener {
            time = "5min"
            timePop!!.dismiss()
        }
        view.tv_time15.setOnClickListener {
            time = "15min"
            timePop!!.dismiss()
        }
        view.tv_time30.setOnClickListener {
            time = "30min"
            timePop!!.dismiss()
        }
        view.tv_time60.setOnClickListener {
            time = "60min"
            timePop!!.dismiss()
        }
        timePop?.contentView = view
    }


    //初始化k线图
    private fun initChart(chart: MyCombinedChart) {
        chart.setScaleEnabled(true)  //启用图表缩放事件
        chart.setDrawBorders(true)//是否绘制边线
        chart.setBorderWidth(1.toFloat())//边线宽度，单位dp
        chart.isDragEnabled = true//启用图表拖拽事件
        chart.isScaleYEnabled = false//启用Y轴上的缩放
        chart.setBorderColor(ContextCompat.getColor(this, R.color.border_color))//边线颜色
        chart.description.isEnabled = false//右下角对图表的描述信息
        chart.isAutoScaleMinMaxEnabled = true
        chart.isDragDecelerationEnabled = true
        chart.minOffset = 0f
        chart.setExtraOffsets(0f, 0f, 3f, 3f)

        val lineChartLegend: Legend = kline_chart.legend
        lineChartLegend.isEnabled = false //是否绘制 Legend 图例
//        lineChartLegend.form = Legend.LegendForm.CIRCLE

        //bar x y轴
        val xAxisKline = chart.xAxis
        xAxisKline.setDrawLabels(true) //是否显示X坐标轴上的刻度，默认是true
        xAxisKline.setDrawGridLines(false)//是否显示X坐标轴上的刻度竖线，默认是true
        xAxisKline.setDrawAxisLine(false) //是否绘制坐标轴的线，即含有坐标的那条线，默认是true
        xAxisKline.enableGridDashedLine(10f, 10f, 0f)//虚线表示X轴上的刻度竖线(float lineLength, float spaceLength, float phase)三个参数，1.线长，2.虚线间距，3.虚线开始坐标
        xAxisKline.gridColor = Color.RED
        xAxisKline.textColor = ContextCompat.getColor(this, R.color.text_color_common)//设置字的颜色
        xAxisKline.position = XAxis.XAxisPosition.BOTTOM//设置值显示在什么位置
        xAxisKline.axisMinimum = -0.5f
//        xAxisKline.axisMaximum = 0.5f
        xAxisKline.textSize = 10f
        xAxisKline.setAvoidFirstLastClipping(true)//设置首尾的值是否自动调整，避免被遮挡

        val axisRight = chart.axisRight
        axisRight.setDrawGridLines(true)
        axisRight.setDrawAxisLine(false)
        axisRight.setDrawZeroLine(false)
        axisRight.setDrawLabels(true)
        axisRight.enableGridDashedLine(10f, 10f, 0f)   //设置虚线
        axisRight.textColor = ContextCompat.getColor(this, R.color.text_color_common)
//        axisRight.setGridColor(getResources().getColor(R.color.minute_grayLine));
        axisRight.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
        axisRight.setLabelCount(4, false) //第一个参数是Y轴坐标的个数，第二个参数是 是否不均匀分布，true是不均匀分布
//        axisRight.spaceTop = 10f//距离顶部留白
        axisRight.minWidth = 30f
        axisRight.maxWidth = 30f
        axisRight.maxWidth = resources.getDimension(R.dimen.k_line_axis_right_width)
        axisRight.minWidth = resources.getDimension(R.dimen.k_line_axis_right_width)


        val axisLeft = chart.axisLeft
        axisLeft.setDrawLabels(false)
        axisLeft.setDrawGridLines(false)
        axisLeft.setDrawAxisLine(false)
        axisLeft.setLabelCount(4, false) //第一个参数是Y轴坐标的个数，第二个参数是 是否不均匀分布，true是不均匀分布
        chart.dragDecelerationFrictionCoef = 0.2f

        chart.animateXY(2000, 2000)
    }

    private fun setValueFormatter(kLineDatas: MutableList<KLineBean>) {
        if (kLineDatas.isEmpty()) return
        kline_chart.xAxis.setValueFormatter { value, axis ->
            if (value.toInt() >= kLineDatas.size) {
                "00:00"
            } else {
                if (tabPosition == 7) {
                    com.fengniao.news.util.DateUtils.longToString("MM-dd"
                            , kLineDatas[value.toInt()].date.toLong())
                } else {
                    com.fengniao.news.util.DateUtils.longToString("HH:mm"
                            , kLineDatas[value.toInt()].date.toLong())
                }
            }
        }

        kline_chart.axisRight.setValueFormatter { value, axis ->
            SymbolInterceptUtils.interceptData(value.toString(),
                    symbol, "price")
        }
        vol_chart.axisRight.setValueFormatter { value, axis ->
            SymbolInterceptUtils.interceptData(value.toString(),
                    symbol, "volume")
        }
    }


    //设置分时图数据
    private fun setMinuteChartData(datas: KLineDataPares) {
        kline_chart.clear()
        datas.initKLineMA(datas.getkLineDatas())
        if (kLineDataPares == null) return
        val lineSet = LineDataSet(kLineDataPares!!.minuteEntries, "")
        lineSet.isHighlightEnabled = true  //开启高亮线
        lineSet.highLightColor = ContextCompat.getColor(this, R.color.colorHighlight)
        lineSet.setDrawValues(false)
        lineSet.color = Color.WHITE
        lineSet.axisDependency = YAxis.AxisDependency.LEFT
        lineSet.setDrawCircles(false)

        val sets = mutableListOf<ILineDataSet>()
        sets.add(lineSet)
        sets.add(getMaLine(10, datas.ma10DataL))

        val lineData = LineData(sets)

        val combinedData = CombinedData()

        combinedData.setData(lineData)

        setHandler(datas, kline_chart)

        kline_chart.data = combinedData
        kline_chart.xAxis.setAvoidFirstLastClipping(true)
        kline_chart.invalidate()
        kline_chart.xAxis.axisMaximum = combinedData.getXMax() + 0.5f
        kline_chart.moveViewToX((kLineDataPares!!.minuteEntries.size).toFloat())
    }

    //设置k线图数据
    private fun setKLineData(datas: KLineDataPares) {
        kline_chart.clear()
        datas.initKLineMA(datas.getkLineDatas())
        val candleData = CandleData()
        val candleDataSet = CandleDataSet(datas.candleEntries, "")
        candleDataSet.axisDependency = YAxis.AxisDependency.LEFT
        candleDataSet.shadowWidth = 1f
        //开盘价低于收盘价的颜色
        candleDataSet.decreasingColor = ContextCompat.getColor(this, R.color.colorDrop)
        candleDataSet.decreasingPaintStyle = Paint.Style.FILL
        //开盘价高于收盘价的颜色
        candleDataSet.increasingColor = ContextCompat.getColor(this, R.color.colorRise)
        candleDataSet.increasingPaintStyle = Paint.Style.FILL
        //开盘价等于收盘价的颜色
        candleDataSet.neutralColor = ContextCompat.getColor(this, R.color.colorRise)
        //阴影和蜡烛图颜色相同
        candleDataSet.shadowColorSameAsCandle = true
        //高亮线的宽度
        candleDataSet.highlightLineWidth = 0.5f
        candleDataSet.barSpace = 0.3f
        candleDataSet.setDrawIcons(false)
        candleDataSet.setDrawValues(true)
        candleDataSet.valueTextColor = Color.WHITE
        candleDataSet.isHighlightEnabled = false

        candleDataSet.valueTextSize = 10f

        candleData.addDataSet(candleDataSet)

        val lineSet = LineDataSet(datas.minuteEntries, "")
        lineSet.isHighlightEnabled = true  //开启高亮线
        lineSet.highLightColor = ContextCompat.getColor(this, R.color.colorHighlight)
        lineSet.setDrawValues(false)
        lineSet.color = Color.BLACK
        lineSet.axisDependency = YAxis.AxisDependency.LEFT
        lineSet.setDrawCircles(false)
        lineSet.isVisible = false

        val sets = mutableListOf<ILineDataSet>()
        sets.add(lineSet)
        sets.add(getMaLine(5, datas.ma5DataL))
        sets.add(getMaLine(10, datas.ma10DataL))
//        sets.add(getMaLine(20, datas.ma20DataL))
//        sets.add(getMaLine(30, datas.ma30DataL))

        val lineData = LineData(sets)

        val combinedData = CombinedData()

        combinedData.setData(lineData)
        combinedData.setData(candleData)

        setHandler(datas, kline_chart)

        kline_chart.data = combinedData
        kline_chart.invalidate()
        kline_chart.xAxis.setAvoidFirstLastClipping(true)
        kline_chart.xAxis.axisMaximum = combinedData.getXMax() + 0.5f

        kline_chart.moveViewToX((datas.candleEntries.size).toFloat())
    }


    //设置条形图数据
    private fun setVolData(datas: KLineDataPares) {
        vol_chart.clear()
        datas.initVolumeMA(datas.getkLineDatas())
        val set = BarDataSet(datas.barEntries, "")
        set.isHighlightEnabled = false  //开启高亮线
//        set.highLightAlpha = 255
//        set.highLightColor = Color.BLACK
        set.setDrawValues(false)
        val list = java.util.ArrayList<Int>()
        list.add(ContextCompat.getColor(this, R.color.colorRise))
        list.add(ContextCompat.getColor(this, R.color.colorDrop))
        set.colors = list
        set.axisDependency = YAxis.AxisDependency.LEFT
        val groupSpace = 0.06f
        val barSpace = 0.02f // x2 dataset
        val barWidth = 0.45f

        val barData = BarData(set)
        barData.barWidth = barWidth

        val lineSet = LineDataSet(datas.volEntry, "")
        lineSet.isHighlightEnabled = true  //开启高亮线
        lineSet.highLightColor = ContextCompat.getColor(this, R.color.colorHighlight)
        lineSet.setDrawHorizontalHighlightIndicator(false)
        lineSet.setDrawValues(false)
        lineSet.color = Color.BLACK
        lineSet.axisDependency = YAxis.AxisDependency.LEFT

        lineSet.setDrawCircles(false)
        lineSet.isVisible = false

        val sets = mutableListOf<ILineDataSet>()
        sets.add(lineSet)
        sets.add(getMaLine(5, datas.ma5DataV))
        sets.add(getMaLine(10, datas.ma10DataV))
//        sets.add(getMaLine(20, datas.ma20DataV))
//        sets.add(getMaLine(30, datas.ma30DataV))

        val lineData = LineData(sets)
        lineData.setValueTextColor(Color.WHITE)


        val combinedData = CombinedData()
        combinedData.setData(barData)
        combinedData.setData(lineData)

        setHandler(datas, vol_chart)

        vol_chart.data = combinedData
        vol_chart.invalidate()
        vol_chart.xAxis.setAvoidFirstLastClipping(true)
        vol_chart.xAxis.axisMaximum = combinedData.getXMax() + 0.5f

        vol_chart.moveViewToX((datas.candleEntries.size).toFloat())
    }

    private fun getMaLine(ma: Int, lineEntries: java.util.ArrayList<Entry>): LineDataSet {
        val lineDataSetMa = LineDataSet(lineEntries, "")
//        if (ma == 5) {
//            lineDataSetMa.isHighlightEnabled = false
//            lineDataSetMa.highLightColor = ContextCompat.getColor(this, R.color.colorRise)
//        } else {/*此处必须得写*/
//            lineDataSetMa.isHighlightEnabled = false
//        }
        lineDataSetMa.isHighlightEnabled = false
        lineDataSetMa.setDrawValues(false)
        when (ma) {
            5 -> lineDataSetMa.color = ContextCompat.getColor(this, R.color.ma5)
            10 -> lineDataSetMa.color = ContextCompat.getColor(this, R.color.ma10)
            20 -> lineDataSetMa.color = ContextCompat.getColor(this, R.color.ma20)
            else -> lineDataSetMa.color = ContextCompat.getColor(this, R.color.ma30)
        }
        lineDataSetMa.setDrawHorizontalHighlightIndicator(false)
        lineDataSetMa.lineWidth = 1f
        lineDataSetMa.setDrawCircles(false)
        lineDataSetMa.axisDependency = YAxis.AxisDependency.LEFT

        return lineDataSetMa
    }


    /**
     * 初始化公共数据
     */
    private fun initCharData() {
        kLineDataPares = KLineDataPares()
        getData()
    }


    private lateinit var mSocketClient: WebSocketClient
    private fun getData() {
//        wss://www.cointiger.com/exchange-market/ws
        mSocketClient = object : WebSocketClient(URI(ApiConstants.SOCKET_ADDRESS)) {
            override fun onOpen(handshakedata: ServerHandshake?) {
                Log.i("test", "onOpen")
                Thread(Runnable {
                    //K线历史数据
                    mSocketClient.send(getHistorySendMsg(time))
                    //交易数据
                    mSocketClient.send(getTransactionSendMsg())
                    //最新成交历史
                    mSocketClient.send(getHistoryNewSendMsg())
                    //获取最近24小时行情g
                    mSocketClient.send(getOneDayQuotesSendMsg())
                }).start()
            }

            override fun onClose(code: Int, reason: String?, remote: Boolean) {
                Log.i("test", "onClose")
            }


            override fun onMessage(bytes: ByteBuffer?) {
                super.onMessage(bytes)
                Log.i("test", GZIPUtils.uncompressToString(bytes?.array()))
                handleData(GZIPUtils.uncompressToString(bytes?.array()))
            }

            override fun onMessage(message: String?) {
                Log.i("test", "onMessage")
            }

            override fun onError(ex: Exception?) {
                Log.i("test", "onError")
            }

        }
        Thread(Runnable {
            mSocketClient.connect()
            Log.i("test", "connect")
        }).start()
    }

    //订阅K线历史消息
    private fun reqKlineHistoryData() {
        if (!mSocketClient.isOpen) return
        Thread(Runnable {
            mSocketClient.send(getHistorySendMsg("1min"))
            mSocketClient.send(getHistorySendMsg("5min"))
            mSocketClient.send(getHistorySendMsg("15min"))
            mSocketClient.send(getHistorySendMsg("30min"))
            mSocketClient.send(getHistorySendMsg("60min"))
            mSocketClient.send(getHistorySendMsg("1day"))
        }).start()
    }


    //发送信息
    private fun sendMessage(msg: String) {
        Log.i("test", msg)
        if (!mSocketClient.isOpen) {
            return
        }
        Thread(Runnable {
            mSocketClient.send(msg)
        }).start()
    }

    fun handleData(data: String) {
        Log.i("test", data)
        var json: JSONObject? = null
        try {
            json = JSONObject(data)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        //包含data字段的数据是K线历史数据
        if (!json?.isNull("data")!!) {
            val message = handler.obtainMessage()
            if (json.getString("channel") == newChannel) {
                message.what = 5
                message.obj = json.optJSONArray("data")
            } else {
                message.what = 0
                message.obj = data
            }
            handler.sendMessage(message)
        }


        if (!json.isNull("tick")) {
            if (json.getString("channel") == newChannel) {
                val message = handler.obtainMessage()
                message.what = 1
                message.obj = data
                handler.sendMessage(message)
                Log.i("newChannel", data)
            }
            if (json.getString("channel") == detailChannel) {
                val message = handler.obtainMessage()
                message.what = 2
                message.obj = data
                handler.sendMessage(message)
            }
            if (json.getString("channel") == kLineChannel) {
                val message = handler.obtainMessage()
                message.what = 3
                message.obj = data
                handler.sendMessage(message)
            }
            if (json.getString("channel") == oneDayQuotesChannel) {
                val message = handler.obtainMessage()
                message.what = 4
                message.obj = data
                handler.sendMessage(message)
            }
        }

    }


    private fun setKlineMarkerView(mData: KLineDataPares) {
        val xMarkerView = XMarkerView(this, R.layout.my_markerview)
        //data选中后y轴上横向的线
        val yMarkerView = YMarkerView(this, R.layout.my_markerview)
        kline_chart.setMarkers(xMarkerView, yMarkerView, mData)

    }

    private fun setVolMarkerView(mData: KLineDataPares) {
        vol_chart.setMarkers(null, null, mData)
    }


    private fun setLine(lineEntries: MutableList<Entry>): LineDataSet {
        val lineDataSet = LineDataSet(lineEntries, "")
        lineDataSet.setHighlightEnabled(false)
        lineDataSet.setDrawValues(false)
        lineDataSet.axisDependency = YAxis.AxisDependency.RIGHT
        lineDataSet.color = Color.WHITE
        lineDataSet.lineWidth = 1f
        lineDataSet.setDrawCircles(false)
        return lineDataSet
    }


    private fun setHandler(dataParse: KLineDataPares, combinedChart: MyCombinedChart) {
        val viewPortHandlerBar = combinedChart.viewPortHandler
        viewPortHandlerBar.setMaximumScaleX(culcMaxscale(dataParse.getkLineDatas().size))
        val touchmatrix = viewPortHandlerBar.matrixTouch
        viewPortHandlerBar.setMaximumScaleX(6f)
        val xscale = 3f
        //矩阵缩小
        touchmatrix.postScale(xscale, 1f)
    }


    private fun initListener() {
        kline_chart.onChartGestureListener = CoupleChartGestureListener(kline_chart, vol_chart)
        vol_chart.onChartGestureListener = CoupleChartGestureListener(vol_chart, kline_chart)
        kline_chart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onNothingSelected() {
                vol_chart.highlightValues(null)
            }

            override fun onValueSelected(e: Entry?, h: Highlight?) {
                if (h == null) return
                val highlight = Highlight(h.x, Float.NaN, h.xPx, h.yPx, h.dataSetIndex, h.axis)
                highlight.dataIndex = h.dataIndex
                vol_chart.highlightValues(arrayOf(h))
                updateText(highlight.x.toInt())
            }
        })
        vol_chart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onNothingSelected() {
                kline_chart.highlightValues(null)
            }

            override fun onValueSelected(e: Entry?, h: Highlight?) {
                if (h == null) return
                val highlight = Highlight(h.x, Float.NaN, h.xPx, h.yPx, h.dataSetIndex, h.axis)
                highlight.dataIndex = h.dataIndex
                kline_chart.highlightValues(arrayOf(h))
                if (e != null)
                    updateText(e.x.toInt())
            }
        })
//        kline_chart.setOnTouchListener(ChartInfoViewHandler(kline_chart))
//        vol_chart.setOnTouchListener(ChartInfoViewHandler(vol_chart))
    }


    private fun updateText(index: Int) {
        tv_high.visibility = View.VISIBLE
        tv_low.visibility = View.VISIBLE
        tv_open.visibility = View.VISIBLE
        tv_close.visibility = View.VISIBLE
        val data = kLineDataPares
        val kLineDatas = data!!.getkLineDatas()

        if (index >= 0 && index < kLineDatas.size) {
            val klData = kLineDatas.get(index)
            tv_open.text = getString(R.string.open) + " " + SymbolInterceptUtils.interceptData(klData.open.toString(), symbol, "price")
            tv_close.text = getString(R.string.close) + " " + SymbolInterceptUtils.interceptData(klData.close.toString(), symbol, "price")
            tv_high.text = getString(R.string.high) + " " + SymbolInterceptUtils.interceptData(klData.high.toString(), symbol, "price")
            tv_low.text = getString(R.string.low) + " " + SymbolInterceptUtils.interceptData(klData.low.toString(), symbol, "price")
            tv_vol.text = getString(R.string.vol) + " " + SymbolInterceptUtils.interceptData(klData.vol.toString(), symbol, "volume")
        }


        if (null != data.ma5DataV && data.ma5DataV.size > 0) {
            if (index > data.ma5DataV.size) return
            if (data.ma5DataV[index].y.isNaN()) {
                tv_ma5.text = "MA5:00"
                return
            }
            tv_ma5.text = "MA5:" + SymbolInterceptUtils.interceptData(data.ma5DataV[index].y.toString(),
                    symbol, "volume")
        }
        if (null != data.ma10DataV && data.ma10DataV.size > 0) {
            if (index > data.ma10DataV.size) return
            if (data.ma10DataV[index].y.isNaN()) {
                tv_ma10.text = "MA10:00"
                return
            }
            tv_ma10.text = "MA10:" + SymbolInterceptUtils.interceptData(data.ma10DataV[index].y.toString(),
                    symbol, "volume")
        }
//        if (null != data.getMa20DataV() && data.getMa20DataV().size > 0) {
//            if (index >= 0 && index < data.ma20DataV.size)
//                mTvKMa20.setText(MyUtils.getDecimalFormatVol(data.getMa20DataL().get(index).getVal()))
//        }
//        if (null != data.ma30DataV && data.ma30DataV.size > 0) {
//            if (index > data.ma30DataV.size) return
//            if (data.ma30DataV[index].y.isNaN()) {
//                tv_ma30.text = "00"
//                return
//            }
//            tv_ma30.text = SymbolInterceptUtils.interceptData(data.ma30DataV[index].y.toString(),
//                    symbol, "volume")
//        }
    }

    private fun addMinuteData(klineDatas: ArrayList<KLineBean>, isRepeat: Boolean) {
        val lineData = kline_chart.lineData
        if (lineData != null) {
            val index = getDataSetIndexCount(lineData)
            val lineDataSet: LineDataSet? = lineData.getDataSetByIndex(0) as LineDataSet//分时线
            if (lineDataSet != null) {
                if (isRepeat && lineDataSet.entryCount > 0) {
                    lineDataSet.removeEntry(lineDataSet.entryCount - 1)
                }
                lineDataSet.addEntry(Entry(lineDataSet.entryCount.toFloat(), klineDatas[klineDatas.lastIndex].close))
            }

            val lineDataSet10: LineDataSet? = lineData.getDataSetByIndex(1) as LineDataSet//十日均线;
            if (lineDataSet10 != null) {
                if (isRepeat && lineDataSet10.entryCount > 0) {
                    lineDataSet10.removeEntry(lineDataSet10.entryCount - 1)
                }
                lineDataSet10.addEntry(Entry(lineDataSet10.entryCount.toFloat(), KMAEntity.getLastMA(klineDatas, 10)))
            }
        }
    }


    private fun addKlineData(dataParse: KLineDataPares, klineDatas: ArrayList<KLineBean>, isRepeat: Boolean) {
        val candleData = kline_chart.candleData
        val lineData = kline_chart.lineData

        val i = klineDatas.size - 1
        if (candleData == null) return
        val count = candleData.dataSetCount
        if (count < 1) return
        val candleDataSet = candleData.getDataSetByIndex(0)
        if (isRepeat && candleDataSet.entryCount > 0) {
            candleDataSet.removeEntry(candleDataSet.entryCount - 1)
        }
        candleDataSet.addEntry(CandleEntry(candleDataSet.entryCount.toFloat(), klineDatas[i].high, klineDatas[i].low
                , klineDatas[i].open, klineDatas[i].close))

//        if (candleData != null) {
//            val indexLast = getLastDataSetIndex(candleData)
//            var lastSet: CandleDataSet? = candleData.getDataSetByIndex(indexLast) as CandleDataSet
//
//            if (lastSet == null) {
//                lastSet = createCandleDataSet()
//                candleData.addDataSet(lastSet)
//            }
//            count = lastSet.entryCount
//
//            //            combinedData.addXValue(xVals);
//            // 位最后一个DataSet添加entry
//            candleData.addEntry(CandleEntry(count.toFloat(), klineDatas[i].high, klineDatas[i].low
//                    , klineDatas[i].open, klineDatas[i].close), indexLast)
//        }

        if (lineData != null) {
            val index = getDataSetIndexCount(lineData)
            val lineDataSet: LineDataSet? = lineData.getDataSetByIndex(0) as LineDataSet//分时线
            if (lineDataSet != null) {
                if (isRepeat && lineDataSet.entryCount > 0) {
                    lineDataSet.removeEntry(lineDataSet.entryCount - 1)
                }
                lineDataSet.addEntry(Entry(lineDataSet.entryCount.toFloat(), klineDatas[klineDatas.lastIndex].close))
            }

            if (index < 2) return
            val lineDataSet5: LineDataSet? = lineData.getDataSetByIndex(1) as LineDataSet//五日均线;
            if (lineDataSet5 != null) {
                if (isRepeat && lineDataSet5.entryCount > 0) {
                    lineDataSet5.removeEntry(lineDataSet5.entryCount - 1)
                }
                lineDataSet5.addEntry(Entry(lineDataSet5.entryCount.toFloat(), KMAEntity.getLastMA(klineDatas, 5)))
            }
            val lineDataSet10: LineDataSet? = lineData.getDataSetByIndex(2) as LineDataSet//十日均线;
            if (lineDataSet10 != null) {
                if (isRepeat && lineDataSet10.entryCount > 0) {
                    lineDataSet10.removeEntry(lineDataSet10.entryCount - 1)
                }
                lineDataSet10.addEntry(Entry(lineDataSet10.entryCount.toFloat(), KMAEntity.getLastMA(klineDatas, 10)))
            }
            //            val lineDataSet20: LineDataSet? = lineData.getDataSetByIndex(3) as LineDataSet//二十日均线;
//            val lineDataSet30: LineDataSet? = lineData.getDataSetByIndex(4) as LineDataSet//三十日均线;

//            if (lineDataSet20 != null) {
//                dataParse.ma20DataL.add(Entry(count.toFloat(), KMAEntity.getLastMA(klineDatas, 20)))
//                lineData.addEntry(dataParse.ma20DataL[dataParse.ma20DataL.size - 1], 3)
//            }
//
//            if (lineDataSet30 != null) {
//                dataParse.ma30DataL.add(Entry(count.toFloat(), KMAEntity.getLastMA(klineDatas, 30)))
//                lineData.addEntry(dataParse.ma30DataL[dataParse.ma30DataL.size - 1], 4)
//            }
        }
    }

    private fun addVolumeData(dataParse: KLineDataPares, klineDatas: ArrayList<KLineBean>, isRepeat: Boolean) {
        val barData = vol_chart.barData
        val lineData = vol_chart.lineData


        val i = klineDatas.size - 1
        val count = barData.dataSetCount
        if (count < 1) return
        val barDataSet = barData.getDataSetByIndex(0)
        if (isRepeat && barDataSet.entryCount > 0) {
            barDataSet.removeEntry(barDataSet.entryCount - 1)
        }
        barDataSet.addEntry(BarEntry(barDataSet.entryCount.toFloat(), klineDatas[i].vol, klineDatas[i]))
//        if (barData != null) {
//            val indexLast = getLastDataSetIndex(barData)
//            var lastSet: BarDataSet? = barData.getDataSetByIndex(indexLast) as BarDataSet
//
//            if (lastSet == null) {
//                lastSet = createBarDataSet()
//                barData.addDataSet(lastSet)
//            }
//            count = lastSet.entryCount
//
//            // 位最后一个DataSet添加entry
//            barData.addEntry(BarEntry(count.toFloat(), klineDatas[i].vol, klineDatas[i]), indexLast)
//        }
//        dataParse.barEntries.add(BarEntry(i.toFloat(), klineDatas[i].vol, klineDatas[i]))

        if (lineData != null) {
            val index = getDataSetIndexCount(lineData)

            val lineDataSet: LineDataSet? = lineData.getDataSetByIndex(0) as LineDataSet//分时线
            if (lineDataSet != null) {
                if (isRepeat && lineDataSet.entryCount > 0) {
                    lineDataSet.removeEntry(lineDataSet.entryCount - 1)
                }
                lineDataSet.addEntry(Entry(lineDataSet.entryCount.toFloat(), klineDatas[klineDatas.lastIndex].vol))
            }

            if (index < 2) return
            val lineDataSet5: LineDataSet? = lineData.getDataSetByIndex(1) as LineDataSet//五日均线;
            val lineDataSet10: LineDataSet? = lineData.getDataSetByIndex(2) as LineDataSet//十日均线;
//            val lineDataSet20: LineDataSet? = lineData.getDataSetByIndex(3) as LineDataSet//二十日均线;
//            val lineDataSet30: LineDataSet? = lineData.getDataSetByIndex(4) as LineDataSet//三十日均线;


            if (lineDataSet5 != null) {
                if (isRepeat && lineDataSet5.entryCount > 0) {
                    lineDataSet5.removeEntry(lineDataSet5.entryCount - 1)
                }
                lineDataSet5.addEntry(Entry(lineDataSet5.entryCount.toFloat(), VMAEntity.getLastMA(klineDatas, 5)))
            }

            if (lineDataSet10 != null) {
                if (isRepeat && lineDataSet10.entryCount > 0) {
                    lineDataSet10.removeEntry(lineDataSet10.entryCount - 1)
                }
                lineDataSet10.addEntry(Entry(lineDataSet10.entryCount.toFloat(), VMAEntity.getLastMA(klineDatas, 10)))
            }

//            if (lineDataSet20 != null) {
//                dataParse.ma20DataV.add(Entry(count.toFloat(), VMAEntity.getLastMA(klineDatas, 20)))
//                lineData.addEntry(dataParse.ma20DataV[dataParse.ma20DataV.size - 1], 3)
//            }
//
//            if (lineDataSet30 != null) {
//                dataParse.ma30DataV.add(Entry(count.toFloat(), VMAEntity.getLastMA(klineDatas, 30)))
//                lineData.addEntry(dataParse.ma30DataV[dataParse.ma30DataV.size - 1], 4)
//            }
        }
    }


    private fun addData(dataParse: KLineDataPares, klineDatas: ArrayList<KLineBean>
                        , data: KLineBean) {
        val isRepeat = klineDatas.last().date == data.date
        if (isRepeat) {
            klineDatas.remove(klineDatas.last())
        }
        klineDatas.add(data)
        if (tabPosition == 1) {
            addMinuteData(klineDatas, isRepeat)
        } else {
            addKlineData(dataParse, klineDatas, isRepeat)
        }
        addVolumeData(dataParse, klineDatas, isRepeat)
    }

    private fun getSum(a: Int, b: Int, datas: ArrayList<KLineBean>): Float {
        return (a..b)
                .map { datas[it].close }
                .sum()
    }

    private fun getJSum(a: Int, b: Int, datas: ArrayList<KLineBean>): Float {
        return (a..b)
                .map { datas[it].vol }
                .sum()
    }

    /**
     * 获取最后一个CandleDataSet的索引
     */
    private fun getLastDataSetIndex(candleData: CandleData): Int {
        val dataSetCount = candleData.dataSetCount
        return if (dataSetCount > 0) dataSetCount - 1 else 0
    }

    /**
     * 获取最后一个LineDataSet的索引
     */
    private fun getDataSetIndexCount(lineData: LineData): Int {
        val dataSetCount = lineData.dataSetCount
        return if (dataSetCount > 0) dataSetCount - 1 else 0
    }

    /**
     * 获取最后一个CandleDataSet的索引
     */
    private fun getLastDataSetIndex(barData: BarData): Int {
        val dataSetCount = barData.dataSetCount
        return if (dataSetCount > 0) dataSetCount - 1 else 0
    }

    private fun createCandleDataSet(): CandleDataSet {
        val dataSet = CandleDataSet(null, "DataSet 1")
        dataSet.axisDependency = YAxis.AxisDependency.LEFT
        dataSet.valueTextSize = 10f

        return dataSet
    }

    private fun createLineDataSet(): LineDataSet {
        val dataSet = LineDataSet(null, "DataSet 1")
        dataSet.axisDependency = YAxis.AxisDependency.LEFT
        dataSet.valueTextSize = 10f

        return dataSet
    }

    private fun createBarDataSet(): BarDataSet {
        val dataSet = BarDataSet(null, "DataSet 1")
        dataSet.axisDependency = YAxis.AxisDependency.LEFT
        dataSet.valueTextSize = 10f

        return dataSet
    }

    private fun culcMaxscale(count: Int): Float {
        val max = (count / 127 * 5).toFloat()
        return max
    }


    //初始化交易详情记录view
    fun initDetailView() {
        for (i in 0 until 5) {
            val view: View = layoutInflater.inflate(R.layout.item_transaction_detail, null)
            view.tv_price_item.setTextColor(ContextCompat.getColor(this, R.color.colorDrop))
            view.tv_quantity_item.setTextColor(Color.WHITE)
            view.tv_price_item.textSize = 10f
            view.tv_quantity_item.textSize = 10f
            ll_sell_price.addView(view)
            sellViewList.add(view)
            val view1: View = layoutInflater.inflate(R.layout.item_transaction_detail, null)
            view1.tv_price_item.setTextColor(ContextCompat.getColor(this, R.color.colorRise))
            view1.tv_quantity_item.setTextColor(Color.WHITE)
            view1.tv_price_item.textSize = 10f
            view1.tv_quantity_item.textSize = 10f
            ll_buy_price.addView(view1)
            buyViewList.add(view1)
        }
        for (i in 0 until 10) {
            val view: View = layoutInflater.inflate(R.layout.item_quotes_deal, null)
            ll_deal.addView(view)
            transactionViewList.add(view)
        }
    }

    //更新实时页面
    fun refreshNewView(datas: List<NewTransactionData.NewTransactionDetail>) {
        newTransactions.addAll(datas)
        if (newTransactions.isEmpty()) return
        while (newTransactions.size > 10) {
            newTransactions.removeAt(0)
        }
        for (i in 0 until 10) {
            val view = transactionViewList[i]
            view.tv_time.text = newTransactions[newTransactions.size - 1 - i].ds.substring(11)
            if (newTransactions[newTransactions.size - 1 - i].side == "BUY") {
                view.tv_price.setTextColor(ContextCompat.getColor(this, R.color.colorRise))
            } else {
                view.tv_price.setTextColor(ContextCompat.getColor(this, R.color.colorDrop))
            }
            view.tv_price.text = SymbolInterceptUtils.interceptData(
                    newTransactions[newTransactions.size - 1 - i].price,
                    symbol, "price")
            view.tv_quantity.text = SymbolInterceptUtils.interceptData(
                    newTransactions[newTransactions.size - 1 - i].vol,
                    symbol, "volume")
            if (newTransactions.size - 1 - i == 0) break
        }
    }

    //更新详情页面
    fun refreshDetailView(transactionData: TransactionData) {
        for (i in 0 until sellViewList.size) {
            if (transactionData.tick.asks.size > sellViewList.size) {

                /**
                 * 移除大值
                 */
                val subList = transactionData.tick.asks.subList(transactionData.tick.asks.size-sellViewList.size,transactionData.tick.asks.size)
                for (ii in subList){
                    Log.d(TAG,"======sub:::=="+ii.toString())
                }

                sellViewList[i].tv_price_item.text = SymbolInterceptUtils.interceptData(
                        subList[i].get(0).toString().replace("\"", "").trim(),
                        symbol, "price")
                sellViewList[i].tv_quantity_item.text = SymbolInterceptUtils.interceptData(
                        subList[i].get(1).toString(),
                        symbol, "volume")




//                sellViewList[i].tv_price_item.text = SymbolInterceptUtils.interceptData(
//                        transactionData.tick.asks[i].get(0).toString().replace("\"", "").trim(),
//                        symbol, "price")
//                sellViewList[i].tv_quantity_item.text = SymbolInterceptUtils.interceptData(
//                        transactionData.tick.asks[i].get(1).toString(),
//                        symbol, "volume")
            } else {
                val temp = sellViewList.size - transactionData.tick.asks.size
                sellViewList[i].tv_price_item.text = "--"
                sellViewList[i].tv_quantity_item.text = "--"
                if (i >= temp) {
                    sellViewList[i].tv_price_item.text = SymbolInterceptUtils.interceptData(
                            transactionData.tick.asks[i - temp].get(0).toString().replace("\"", "").trim(),
                            symbol, "price")
                    sellViewList[i].tv_quantity_item.text = SymbolInterceptUtils.interceptData(
                            transactionData.tick.asks[i - temp].get(1).toString(),
                            symbol, "volume")
                }
            }

            if (transactionData.tick.buys.size > i) {
                buyViewList[i].tv_price_item.text = SymbolInterceptUtils.interceptData(transactionData.tick.buys[i].get(0).toString().replace("\"", "").trim(),
                        symbol, "price")

                buyViewList[i].tv_quantity_item.text = SymbolInterceptUtils.interceptData(transactionData.tick.buys[i].get(1).toString().trim(),
                        symbol, "volume")
            } else {
                buyViewList[i].tv_price_item.text = "--"
                buyViewList[i].tv_quantity_item.text = "--"
            }
        }
    }


    private fun clearDetailViewData() {
        for (i in 0 until 10) {
            transactionViewList[i].tv_time.text = "--"
            transactionViewList[i].tv_price.text = "--"
            transactionViewList[i].tv_quantity.text = "--"
        }
        for (i in 0 until 5) {
            sellViewList[i].tv_price_item.text = "--"
            sellViewList[i].tv_quantity_item.text = "--"
            buyViewList[i].tv_price_item.text = "--"
            buyViewList[i].tv_quantity_item.text = "--"
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ChooseSymbolActivity.CODE_CHOOSE_SYMBOL &&
                resultCode == RESULT_OK) {
            val symbol = data?.getParcelableExtra<SymbolData.Symbol>("symbol")
            if (symbol != null) {
                if (symbol.key == this.symbol) return
                title = symbol.name
                if (mSocketClient.isOpen) {
                    resetAllData()
                    resetAllView()
                    this.symbol = symbol.key
                    sendMessage(getHistorySendMsg(time))
                    sendMessage(getHistoryNewSendMsg())
                    sendMessage(getOneDayQuotesSendMsg())
                    sendMessage(getTransactionSendMsg())
                } else {
                    this.symbol = symbol.key
                    resetAllView()
                    getData()
                }
            }
        }
    }

    //重置所有view
    private fun resetAllView() {
        tv_quotes_day.text = ""
        iv_quotes_day.setImageResource(0)
        tv_low_day.text = ""
        tv_high_day.text = ""
        tv_quotes_change_day.text = ""
        tv_deal_quantity_day.text = ""
        resetKlineDetailView()
        clearDetailViewData()
    }


    //重置kline详情view
    private fun resetKlineDetailView() {
        kline_chart.clear()
        kline_chart.invalidate()
        vol_chart.clear()
        vol_chart.invalidate()
        tv_open.text = getString(R.string.open)
        tv_high.text = getString(R.string.high)
        tv_low.text = getString(R.string.low)
        tv_close.text = getString(R.string.close)
        tv_vol.text = getString(R.string.vol)
        tv_ma5.text = "MA5"
        tv_ma10.text = "MA10"
//        tv_ma30.text = "MA30"
    }

    override fun onDestroy() {
        super.onDestroy()
        mSocketClient.close()
    }

}



