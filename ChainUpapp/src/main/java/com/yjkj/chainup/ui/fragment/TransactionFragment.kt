package com.yjkj.chainup.ui.fragment


import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Handler
import android.os.Message
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import com.fengniao.news.util.JsonUtils
import com.google.gson.JsonObject
import com.trello.rxlifecycle2.kotlin.bindToLifecycle
import com.yjkj.chainup.R
import com.yjkj.chainup.app.AppConstant
import com.yjkj.chainup.base.BaseListFragment
import com.yjkj.chainup.base.FNAdapter
import com.yjkj.chainup.bean.*
import com.yjkj.chainup.local.SQLManager
import com.yjkj.chainup.manager.LoginManager
import com.yjkj.chainup.net.HttpClient
import com.yjkj.chainup.net.api.ApiConstants
import com.yjkj.chainup.net.retrofit.NetObserver
import com.yjkj.chainup.ui.activity.*
import com.yjkj.chainup.ui.dialog.AssetsPassDialogFragment
import com.yjkj.chainup.util.*
import com.yjkj.chainup.wedegit.FullyLinearLayoutManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_transaction.*
import kotlinx.android.synthetic.main.fragment_transaction_detail.*
import kotlinx.android.synthetic.main.item_list_entrust_new.view.*
import kotlinx.android.synthetic.main.item_transaction_detail.view.*
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import org.json.JSONObject
import java.lang.Exception
import java.math.BigDecimal
import java.net.URI
import java.nio.ByteBuffer


/**
 * 交易页面
 * A simple [Fragment] subclass.
 */
class TransactionFragment : BaseListFragment<EntrustData.Entrust>() {
    val TAG = TransactionFragment::class.java.simpleName

    override fun enableLazyLoad(): Boolean = false

    var priceScale = 0

    var volumeScale = 0

    //可用余额
    var canUseMoney: String = "0"

    var inputPrice: String = "0"

    var inputQuantity: String = "0"

    //账户信息(暂弃用)
    var mAssets: AssetsData.Assets? = null


    //当前币种账户余额信息
    var balanceData: BalanceData? = null


    var list = mutableListOf<String>()
    //币对
    lateinit var symbol: SymbolData.Symbol
    /**
     * 用于订阅实时成交信息
     */
    private var newChannel = ""

    //用于订阅深度盘口
    private var step = "0"
    private var detailChannel = ""


    private var sellViewList = mutableListOf<View>()

    private var buyViewList = mutableListOf<View>()

    private var transactionData: TransactionData? = null

    private var assetCertDialog: AssetsPassDialogFragment? = null

    private var isFirst = true


    companion object {
        //买入
        val TYPE_BUY = 0

        //卖出
        val TYPE_SELL = 1

        //限价交易
        val TYPE_CUSTOM = 0

        //市价交易
        val TYPE_MARCKET = 1
    }

    //交易类型
    var transactionType: Int = 0


    //价格类型
    var priceType = 0

    var handler: Handler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            if (msg?.what == 0) {
                val newTransactionData: NewTransactionData? = JsonUtils.jsonToBean(msg.obj as String, NewTransactionData::class.java)
                if (newTransactionData != null) {
                    refreshNewView(newTransactionData)
                }
            }

            if (msg?.what == 1) {
                transactionData = JsonUtils.jsonToBean(msg.obj as String, TransactionData::class.java)
                if (transactionData != null) {
                    /**
                     *卖盘取最小
                     */
                    transactionData?.tick?.asks?.sortByDescending { it.get(0).asFloat }

                    /**
                     * 买盘取最大
                     */
                    transactionData?.tick?.buys?.sortByDescending { it.get(0).asFloat }
                    refreshDetailView(transactionData!!)
                }
            }
            if (msg?.what == 2) {
                et_price.setText(msg.obj as String)
            }
            if (msg?.what == 3) {
                et_quantity.setText(msg.obj as String)
            }
        }
    }

    override fun setLayoutId(): Int = R.layout.fragment_transaction


    override fun afterCreateView() {
        super.afterCreateView()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            btn_confirm.letterSpacing = if (SystemUtils.isZh()) {
                AppConstant.zh_letter_spacing
            } else {
                AppConstant.en_letter_spacing
            }
        }else{
            btn_confirm.textScaleX = if (SystemUtils.isZh()) {
                AppConstant.zh_letter_spacing
            } else {
                AppConstant.en_letter_spacing
            }
        }
        recycler_view.isNestedScrollingEnabled = false
        initSymbol()
        initView()
        initSocket()
        (activity as MainActivity).onTabClick(0)
    }

    private fun initSymbol() {
        val mSymbol = SQLManager.INSTANCE().searchSymbolByKey("ethbtc")
        if (mSymbol != null) {
            symbol = mSymbol
            return
        }
        val list = mutableListOf<String>()
        list.add("0.00000001")
        list.add("0.000001")
        list.add("0.0001")
        symbol = SymbolData.Symbol("ETH/BTC", "ethbtc", list, 8, 3)
    }


    //指定symbol,用于从k线图跳转过来
    fun setSymbol(key: String) {
        if (TextUtils.isEmpty(key)) return
        if (key == symbol.key) return
        val symbol = SQLManager.INSTANCE().searchSymbolByKey(key) ?: return
        if (mSocketClient.isOpen) {
            //退订以前的币种
            subMessage(getDetailSendMsg(false))
            subMessage(getNewSendMsg(false))
            reset()
            clearDetailView()
            this.symbol.saveSymbol(symbol)
            tv_choose_symbol.text = symbol.name
            updateView()
        } else {
            reset()
            clearDetailView()
            this.symbol.saveSymbol(symbol)
            tv_choose_symbol.text = symbol.name
            initSocket()
        }
        onRefresh()
    }


    private fun initView() {
        assetCertDialog = AssetsPassDialogFragment()
        tv_buy.background = ContextCompat.getDrawable(context!!,R.drawable.bg_tab_top_s)
        selectTab(0)
        initDetailView()
        selectDeepType(100)
        tv_deep_one.background = ContextCompat.getDrawable(context!!,R.drawable.bg_tab_top)
        initListener()
        initLongClick()
    }

    fun selectTab(position: Int) {
        tv_buy.background = null
        tv_sell.background = null
        tv_buy.setTextColor(ContextCompat.getColor(context!!, R.color.colorTabNormal))
        tv_sell.setTextColor(ContextCompat.getColor(context!!, R.color.colorTabNormal))
        updateSymbolInfo()
        when (position) {
            0 -> {
                tv_buy.setTextColor(Color.WHITE)
                tv_buy.background = ContextCompat.getDrawable(context!!,R.drawable.bg_tab_top_s)
                transactionType = TYPE_BUY
                btn_confirm.text = getString(R.string.buy)
                btn_confirm.setBackgroundResource(R.drawable.bg_btn_buy)
                custom_seek_bar.setSeekBarDrawable(R.color.colorRise)
            }
            1 -> {
                tv_sell.setTextColor(Color.WHITE)
                tv_sell.background = ContextCompat.getDrawable(context!!,R.drawable.bg_tab_top_s)
                transactionType = TYPE_SELL
                btn_confirm.text = getString(R.string.sell)
                btn_confirm.setBackgroundResource(R.drawable.bg_btn_sell)
            }
        }
        selectPriceType(priceType)
    }




    override fun onResume() {
        super.onResume()
        updateSymbolInfo()
        if (mList.isEmpty()) {
            loadData()
        }
        if (!LoginManager.checkLogin(context, false)) {
            tv_total_money.text = "--"
            et_price.isFocusableInTouchMode = false
            et_quantity.isFocusableInTouchMode = false
        } else {
            if (!et_quantity.isFocusableInTouchMode) {
                et_quantity.isFocusable = true
                et_quantity.isFocusableInTouchMode = true
                et_quantity.requestFocus()
                et_quantity.findFocus()
            }
            if (!et_price.isFocusableInTouchMode) {
                et_price.isFocusable = true
                et_price.isFocusableInTouchMode = true
                et_price.requestFocus()
                et_price.findFocus()
            }
        }
        //禁止seekbar滑动
//        if (transactionType == TYPE_SELL && inputPrice == 0f) {
//            custom_seek_bar.isCanScroll = false
//        }
    }

    //更新币种相关信息
    private fun updateSymbolInfo() {
        tv_deep_one.text = symbol.dept[0].replace("0.", "").length.toString()
        tv_deep_two.text = symbol.dept[1].replace("0.", "").length.toString()
        tv_deep_three.text = symbol.dept[2].replace("0.", "").length.toString()
        getMoneyUnit()
        getVolumeUnit()
        tv_choose_symbol.text = symbol.name
        tv_header_price.text = getString(R.string.price) + symbol.name.substring(symbol.name.indexOf("/"))
        tv_header_quantity.text = getString(R.string.quantity) + "/" + symbol.name.substring(0, symbol.name.indexOf("/"))
        if (LoginManager.checkLogin(context, false)) {
            getBalance()
//            getMyAssets()
        }
    }


    //重置view和数据
    fun reset() {
        inputQuantity = "0"
        inputPrice = "0"
        et_quantity.setText("")
        et_price.setText("")
        custom_seek_bar.progress = 0
        tv_all_quantity.text = "--"
        tv_all_transaction_money.text = "--"
        tv_total_money.text = "--"
        tv_new_price.text = "--"
    }


    //初始化交易详情记录view
    fun initDetailView() {
        for (i in 0 until 5) {
            /**
             * 卖出交易
             */
            val view: View = layoutInflater.inflate(R.layout.item_transaction_detail, null)
            view.tv_price_item.setTextColor(ContextCompat.getColor(context!!, R.color.colorDrop))
            view.setOnClickListener {
                if (view.tv_price_item.text != "--" && priceType == TYPE_CUSTOM)
                    et_price.setText(view.tv_price_item.text)
            }
            ll_sell_price.addView(view)
            sellViewList.add(view)


            /**
             * 买入交易
             */
            val view1: View = layoutInflater.inflate(R.layout.item_transaction_detail, null)
            view1.tv_price_item.setTextColor(ContextCompat.getColor(context!!, R.color.colorRise))
            view1.setOnClickListener {
                if (view1.tv_price_item.text != "--" && priceType == TYPE_CUSTOM)
                    et_price.setText(view1.tv_price_item.text)
            }
            ll_buy_price.addView(view1)
            buyViewList.add(view1)
        }
    }

    //重置交易详情记录数据
    fun clearDetailView() {
        for (i in 0 until 5) {
            sellViewList[i].tv_price_item.text = "--"
            sellViewList[i].tv_quantity_item.text = "--"
            buyViewList[i].tv_price_item.text = "--"
            buyViewList[i].tv_quantity_item.text = "--"
        }
    }

    private fun updateView() {
        subMessage(getDetailSendMsg())
        subMessage(getNewSendMsg())
    }

    private fun initListener() {
        assetCertDialog!!.mListener = object : AssetsPassDialogFragment.AssetsCertListener {
            override fun onAssetsCert(pass: String) {
                createOrder(pass)
            }
        }
        tv_buy.setOnClickListener {
            selectTab(0)
        }
        tv_choose_symbol.setOnClickListener {
            val intent = Intent(activity, ChooseSymbolActivity::class.java)
            startActivityForResult(intent, ChooseSymbolActivity.CODE_CHOOSE_SYMBOL)
        }

        tv_sell.setOnClickListener {
            selectTab(1)
        }
        tv_deep_one.setOnClickListener {
            selectDeepType(0)
        }
        tv_deep_two.setOnClickListener {
            selectDeepType(1)
        }
        tv_deep_three.setOnClickListener {
            selectDeepType(2)
        }
        tv_limit.setOnClickListener {
            selectPriceType(0)
        }
        tv_market.setOnClickListener {
            selectPriceType(1)
        }
        iv_quotes.setOnClickListener {
            val intent = Intent(context, QuotesDetailActivity::class.java)
            intent.putExtra("name", symbol.name)
            intent.putExtra("symbol", symbol.key)
            activity?.startActivityForResult(intent, 0)
        }
        iv_trust_record.setOnClickListener {
            if (!LoginManager.checkLogin(this, true)) return@setOnClickListener
            val intent = Intent(context, TransactionRecordActivity::class.java)
            intent.putExtra("symbol", symbol)
            jumpToActivity(intent)
        }
//        tv_price_less.setOnClickListener {
//            if (!LoginManager.checkLogin(this, true)) return@setOnClickListener
//            val unit = if (transactionType == TYPE_SELL &&
//                    priceType == TYPE_MARCKET) {
//                getVolumeUnit()
//            } else {
//                getMoneyUnit()
//            }
//            if (TextUtils.isEmpty(unit)) return@setOnClickListener
//            if (BigDecimal(inputPrice).toFloat() > 0f) {
//                inputPrice = BigDecimalUtils.sub(inputPrice, unit).toPlainString()
//                et_price.setText(inputPrice)
//            } else {
//                et_price.setText("")
//            }
//        }
//        tv_price_plus.setOnClickListener {
//            if (!LoginManager.checkLogin(this, true)) return@setOnClickListener
//            val unit = if (transactionType == TYPE_SELL &&
//                    priceType == TYPE_MARCKET) {
//                getVolumeUnit()
//            } else {
//                getMoneyUnit()
//            }
//            if (TextUtils.isEmpty(unit)) return@setOnClickListener
//            inputPrice = BigDecimalUtils.add(inputPrice, unit).toPlainString()
//            et_price.setText(inputPrice)
//        }
//        tv_quantity_less.setOnClickListener {
//            if (!LoginManager.checkLogin(this, true)) return@setOnClickListener
//            val unit = getVolumeUnit()
//            if (TextUtils.isEmpty(unit)) return@setOnClickListener
//            if (BigDecimal(inputQuantity).toFloat() > 0f) {
//                inputQuantity = BigDecimalUtils.sub(inputQuantity, unit).toPlainString()
//                et_quantity.setText(inputQuantity)
//            } else {
//                et_quantity.setText("")
//            }
//        }
//        tv_quantity_plus.setOnClickListener {
//            if (!LoginManager.checkLogin(this, true)) return@setOnClickListener
//            val unit = getVolumeUnit()
//            if (TextUtils.isEmpty(unit)) return@setOnClickListener
//            inputQuantity = BigDecimalUtils.add(inputQuantity, unit).toPlainString()
//            et_quantity.setText(inputQuantity)
//        }

        et_price.setOnClickListener {
            LoginManager.checkLogin(this, true)
        }
        et_quantity.setOnClickListener {
            LoginManager.checkLogin(this, true)
        }

        et_price.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                et_price.setSelection(et_price.text.length)
                //限制数字精度
                if (s!!.toString().contains(".")) {
                    val length = s.toString().substring(s.indexOf(".") + 1).length
                    if (transactionType == TYPE_SELL && priceType == TYPE_MARCKET) {
                        val aimsLength = symbol.volumePrecision
                        if (length > aimsLength) {
                            et_price.setText(s.substring(0, s.indexOf(".") + aimsLength + 1))
                            return
                        }
                    } else {
                        val aimsLength = symbol.pricePrecision
                        if (length > aimsLength) {
                            et_price.setText(s.substring(0, s.indexOf(".") + aimsLength + 1))
                            return
                        }
                    }
                }
                inputPrice = if (TextUtils.isEmpty(s)) {
                    "0"
                } else {
                    s.toString()
                }
                if (inputPrice.startsWith(".")) inputPrice = "0"
                //禁止seekbar滑动
//                custom_seek_bar.isCanScroll = inputPrice != 0f
                if (transactionType == TYPE_BUY) {
                    if (priceType == TYPE_CUSTOM) {
                        if (inputPrice.toFloat() > 0) {
                            //买入且限价交易状态下，价格，可用金额已知，seekbar控制的是可买入数量
                            var vol = BigDecimalUtils.div(canUseMoney, inputPrice, volumeScale).toPlainString()
                            vol = SymbolInterceptUtils.interceptData(vol, symbol.key, "volume")
                            tv_all_quantity.text = vol + symbol.name.substring(0, symbol.name.indexOf("/"))
                        } else {
                            tv_all_quantity.text = "--"
                        }
                        //计算总金额
                        var money = BigDecimalUtils.mul(inputPrice, inputQuantity).toPlainString()
                        money = SymbolInterceptUtils.interceptData(money, symbol.key, "price")
                        tv_all_transaction_money.text = money +
                                symbol.name.substring(symbol.name.indexOf("/") + 1)
                    } else {
                        //买入且市价交易状态下，价格未知，可用金额已知，seekbar控制可用金额
                        tv_all_quantity.text = SymbolInterceptUtils.interceptData(canUseMoney, symbol.key, "price") +
                                symbol.name.substring(symbol.name.indexOf("/") + 1)
                    }
                } else {
                    //当市价卖出状态时，此输入框输入的是商品数量（即币对前半段），seekbar控制此输入框内容
                    if (priceType == TYPE_MARCKET) {
//                        if (canUseMoney != 0f) {
//                            val progress = (inputPrice * 100 / canUseMoney).toInt()
//                            custom_seek_bar.progress = progress
//                        }
                    } else {
                        var money = BigDecimalUtils.mul(inputPrice, inputQuantity).toPlainString()
                        money = SymbolInterceptUtils.interceptData(money, symbol.key, "price")
                        tv_all_transaction_money.text = money +
                                symbol.name.substring(symbol.name.indexOf("/") + 1)
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        et_quantity.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s!!.toString().contains(".")) {
                    val length = s.toString().substring(s.indexOf(".") + 1).length
                    val aimsLength = symbol.volumePrecision
                    if (length > aimsLength) {
                        et_quantity.setText(s.substring(0, s.indexOf(".") + aimsLength + 1))
                        return
                    }
                }
                inputQuantity = if (TextUtils.isEmpty(s)) {
                    "0"
                } else {
                    s.toString()
                }
                if (inputQuantity.startsWith("."))
                    inputQuantity = "0"

                if (transactionType == TYPE_BUY) {
                    if (priceType == TYPE_CUSTOM) {
                        //交易额
                        var money = BigDecimalUtils.mul(inputPrice, inputQuantity).toPlainString()
                        money = SymbolInterceptUtils.interceptData(money, symbol.key, "price")
                        tv_all_transaction_money.text = money +
                                symbol.name.substring(symbol.name.indexOf("/") + 1)
//                        if (canUseMoney != 0f) {
//                            val progress = (inputPrice * inputQuantity * 100 / canUseMoney).toInt()
//                            custom_seek_bar.progress = progress
//                        }
                    }
                } else {
                    if (priceType == TYPE_CUSTOM) {
                        //交易额
                        var money = BigDecimalUtils.mul(inputPrice, inputQuantity).toPlainString()
                        money = SymbolInterceptUtils.interceptData(money, symbol.key, "price")
                        tv_all_transaction_money.text = money +
                                symbol.name.substring(symbol.name.indexOf("/") + 1)
//                        if (canUseMoney != 0f) {
//                            val progress = (inputQuantity * 100 / canUseMoney).toInt()
//                            custom_seek_bar.progress = progress
//                        }
                    }
                }
                et_quantity.setSelection(et_quantity.text.length)

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })


        custom_seek_bar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

                if (transactionType == TYPE_BUY) {
                    if (priceType == TYPE_CUSTOM) {
                        if (inputPrice.toFloat() > 0f) {
                            var result = BigDecimalUtils.div(canUseMoney, inputPrice).toPlainString()
                            result = BigDecimalUtils.mul(result, progress.toString()).toPlainString()
                            result = BigDecimalUtils.div(result, 100.toString(), volumeScale).toPlainString()
                            et_quantity.setText(result)
                        }
                        var result = BigDecimalUtils.mul(inputPrice, inputQuantity).toPlainString()
                        result = BigDecimalUtils.mul(result, progress.toString()).toPlainString()
                        result = BigDecimalUtils.div(result, 100.toString(), priceScale).toPlainString()
                        result = SymbolInterceptUtils.interceptData(result, symbol.key, "price")
                        tv_all_transaction_money.text = result + symbol.name.substring(symbol.name.indexOf("/") + 1)
                    } else {
                        var result = BigDecimalUtils.mul(canUseMoney, progress.toString()).toPlainString()
                        result = BigDecimalUtils.div(result, 100.toString(), priceScale).toPlainString()
                        result = SymbolInterceptUtils.interceptData(result, symbol.key, "price")
                        tv_all_transaction_money.text = result
                        et_price.setText(result)
                    }
                } else {
                    if (priceType == TYPE_CUSTOM) {
                        var result = BigDecimalUtils.mul(canUseMoney, progress.toString()).toPlainString()
                        result = BigDecimalUtils.div(result, 100.toString(), volumeScale).toPlainString()
                        result = SymbolInterceptUtils.interceptData(result, symbol.key, "volume")
                        et_quantity.setText(result)
                        var money = BigDecimalUtils.mul(inputPrice, inputQuantity).toPlainString()
                        money = SymbolInterceptUtils.interceptData(money, symbol.key, "price")
                        tv_all_transaction_money.text = money + symbol.name.substring(symbol.name.indexOf("/") + 1)

                    } else {
                        var result = BigDecimalUtils.mul(canUseMoney, progress.toString()).toPlainString()
                        result = BigDecimalUtils.div(result, 100.toString()).toPlainString()
                        result = SymbolInterceptUtils.interceptData(result, symbol.key, "volume")
                        et_price.setText(result)
                        tv_all_transaction_money.text = result
                    }
                }
//                if (!TextUtils.isEmpty(inputPrice) && priceType == TYPE_MARCKET) {
//                    var result = BigDecimalUtils.div(canUseMoney,inputPrice).toPlainString()
//                    result = BigDecimalUtils.mul(result,progress.toString()).toPlainString()
//                    result = BigDecimalUtils.div(result,100.toString(),volumeScale).toPlainString()
//                    et_quantity.setText(result)
//                }
//                if (transactionType == TYPE_SELL) {
//                    var result = BigDecimalUtils.mul(canUseMoney,progress.toString()).toPlainString()
//                    result = BigDecimalUtils.div(result,100.toString(),)
//                    et_quantity.setText((canUseMoney * progress / 100).toString())
//
//                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        /**
         * 买入 Or 卖出
         */
        btn_confirm.setOnClickListener {
            if (!LoginManager.checkLogin(this, true)) return@setOnClickListener
            if (balanceData == null) return@setOnClickListener

            /**
             * 限价交易
             */
            if (priceType == TYPE_CUSTOM) {
                if (BigDecimalUtils.compareTo(inputPrice, balanceData!!.minPrice) < 0) {
                    UIUtils.showToast(getString(R.string.toast_min_price) + balanceData!!.minPrice)
                    return@setOnClickListener
                }
                if (BigDecimalUtils.compareTo(inputQuantity, balanceData!!.minVolume) < 0) {
                    UIUtils.showToast(getString(R.string.toast_min_vol) + balanceData!!.minVolume)
                    return@setOnClickListener
                }
            } else {
                /**
                 * 市价交易
                 */
//                    Log.d("========", "====input:=volu=" + inputQuantity + "==input : pri"+inputPrice+"=====minVolume:==" + balanceData!!.minVolume+":===minPrice:===="+balanceData!!.minPrice)

                /**
                 * 在市价交易的前提下，无论买入or卖出，都使用的是inputPrice字段...
                 */


                if (transactionType == TYPE_BUY) {
                    /**
                     * 最小价格
                     */
                    if (BigDecimalUtils.compareTo(inputPrice, balanceData!!.minPrice) < 0) {
                        UIUtils.showToast(getString(R.string.toast_min_price) + balanceData!!.minPrice)
                        return@setOnClickListener
                    }
                } else {

                    /**
                     * 最小交易量
                     */
                    if (BigDecimalUtils.compareTo(inputPrice, balanceData!!.minVolume) < 0) {
                        UIUtils.showToast(getString(R.string.toast_min_vol) + balanceData!!.minVolume)
                        return@setOnClickListener
                    }

                }




//                if (BigDecimalUtils.compareTo(inputPrice, balanceData!!.minPrice) < 0) {
//                    if (transactionType == TYPE_BUY) {
//                        UIUtils.showToast(getString(R.string.toast_min_price) + balanceData!!.minPrice)
//                    } else {
//                        UIUtils.showToast(getString(R.string.toast_min_vol) + balanceData!!.minVolume)
//                    }
//                    return@setOnClickListener
//
//                    Log.d("========", "====input:==" + inputQuantity + "=====minVolume:==" + balanceData!!.minVolume)
//
//
            }


            val user = LoginManager.getInstance(context).user ?: return@setOnClickListener

            Log.d(TAG, "---user:---" + user.user.toString())

//            if (user.user.capital_status != 1) {
//                UIUtils.showToast(getString(R.string.toast_not_set_asset_pass))
//                return@setOnClickListener
//            }


            createOrder("")


//            when (user.user.exchange_verify) {
//                1 -> {
//                    createOrder("")
//                }
//                2 -> {
//                    val gap: Long = System.currentTimeMillis() - LoginManager.getInstance().assetsTime
//                    if (gap > AppConstant.twoHour) {
//                        assetCertDialog!!.show(childFragmentManager, "dialog")
//                    } else {
//                        createOrder("")
//                    }
//                }
//                3 -> {
//                    assetCertDialog!!.show(childFragmentManager, "dialog")
//                }
//            }

        }
    }

    private var isPriceLongClick: Boolean = false
        get() = field
        set(value) {
            field = value
        }

    private var isVolumeLongClick = false
        get() = field
        set(value) {
            field = value
        }
    private var isStartPriceSubClick = false
        get() = field
        set(value) {
            field = value
        }

    private var isStartPricePlusClick = false
        get() = field
        set(value) {
            field = value
        }

    private var isStartVolSubClick = false
        get() = field
        set(value) {
            field = value
        }

    private var isStartVolPlusClick = false
        get() = field
        set(value) {
            field = value
        }
    val delayTime = 200L

    private fun initLongClick() {


        /**
         * 价格 （-）
         */
        tv_price_less.setOnTouchListener { v, event ->
            isPriceLongClick = true
            isStartPriceSubClick = true
            if (event.action == MotionEvent.ACTION_DOWN) {
                if (!LoginManager.checkLogin(this, true)) return@setOnTouchListener false
                val unit = if (transactionType == TYPE_SELL &&
                        priceType == TYPE_MARCKET) {
                    getVolumeUnit()
                } else {
                    getMoneyUnit()
                }
                if (TextUtils.isEmpty(unit)) return@setOnTouchListener false
                if (BigDecimal(inputPrice).toFloat() > 0f) {
                    inputPrice = BigDecimalUtils.sub(inputPrice, unit).toPlainString()
                    et_price.setText(inputPrice)
                } else {
                    et_price.setText("")
                    return@setOnTouchListener true
                }
                Thread {
                    while (isPriceLongClick) {
                        Thread.sleep(delayTime)
                        if (!isStartPriceSubClick) continue
                        inputPrice = if (BigDecimal(inputPrice).toFloat() > 0f) {
                            BigDecimalUtils.sub(inputPrice, unit).toPlainString()
                        } else {
                            ""
                        }
                        val msg = handler.obtainMessage()
                        msg.what = 2
                        msg.obj = inputPrice
                        handler.sendMessage(msg)
                    }
                }.start()
                return@setOnTouchListener true
            }
            if (event.action == MotionEvent.ACTION_UP ||
                    event.action == MotionEvent.ACTION_CANCEL) {
                isPriceLongClick = false
                isStartPriceSubClick = false
            }
            true
        }


        /**
         * 价格 （+）
         */
        tv_price_plus.setOnTouchListener { v, event ->
            isPriceLongClick = true
            isStartPricePlusClick = true
            if (event.action == MotionEvent.ACTION_DOWN) {
                if (!LoginManager.checkLogin(this, true)) return@setOnTouchListener false
                val unit = if (transactionType == TYPE_SELL &&
                        priceType == TYPE_MARCKET) {
                    getVolumeUnit()
                } else {
                    getMoneyUnit()
                }
                if (TextUtils.isEmpty(unit)) return@setOnTouchListener false
                inputPrice = BigDecimalUtils.add(inputPrice, unit).toPlainString()
                et_price.setText(inputPrice)
                Thread {
                    while (isPriceLongClick) {
                        Thread.sleep(delayTime)
                        if (!isStartPricePlusClick) continue
                        inputPrice = BigDecimalUtils.add(inputPrice, unit).toPlainString()
                        val msg = handler.obtainMessage()
                        msg.what = 2
                        msg.obj = inputPrice
                        handler.sendMessage(msg)
                    }
                }.start()
                return@setOnTouchListener true
            }
            if (event.action == MotionEvent.ACTION_UP ||
                    event.action == MotionEvent.ACTION_CANCEL) {
                isPriceLongClick = false
                isStartPricePlusClick = false
            }
            true
        }

        /**
         * 交易量 （-）按钮
         */
        tv_quantity_less.setOnTouchListener { v, event ->
            isVolumeLongClick = true
            isStartVolSubClick = true
            if (event.action == MotionEvent.ACTION_DOWN) {
                if (!LoginManager.checkLogin(this, true)) return@setOnTouchListener false
                val unit = getVolumeUnit()
                if (TextUtils.isEmpty(unit)) return@setOnTouchListener false
                if (BigDecimal(inputQuantity).toFloat() > 0f) {
                    inputQuantity = BigDecimalUtils.sub(inputQuantity, unit).toPlainString()
                    et_quantity.setText(inputQuantity)
                } else {
                    et_quantity.setText("")
                    return@setOnTouchListener true
                }
                Thread {
                    while (isVolumeLongClick) {
                        Thread.sleep(delayTime)
                        if (!isStartVolSubClick) continue
                        inputQuantity = if (BigDecimal(inputQuantity).toFloat() > 0f) {
                            BigDecimalUtils.sub(inputQuantity, unit).toPlainString()
                        } else {
                            ""
                        }
                        val msg = handler.obtainMessage()
                        msg.what = 3
                        msg.obj = inputQuantity
                        handler.sendMessage(msg)
                    }
                }.start()
                return@setOnTouchListener true
            }
            if (event.action == MotionEvent.ACTION_UP ||
                    event.action == MotionEvent.ACTION_CANCEL) {
                isVolumeLongClick = false
                isStartVolSubClick = false
            }
            true
        }


        /**
         * 交易量（+）
         */
        tv_quantity_plus.setOnTouchListener { v, event ->
            isVolumeLongClick = true
            isStartVolPlusClick = true
            if (event.action == MotionEvent.ACTION_DOWN) {
                if (!LoginManager.checkLogin(this, true)) return@setOnTouchListener false
                val unit = getVolumeUnit()
                Log.d(TAG, "=====unit:=====" + unit)
                if (TextUtils.isEmpty(unit)) return@setOnTouchListener false
                inputQuantity = BigDecimalUtils.add(inputQuantity, unit).toPlainString()
                Log.d(TAG, "===inputQuantity:===" + inputQuantity)
                et_quantity.setText(inputQuantity)
                Thread {
                    while (isVolumeLongClick) {
                        Thread.sleep(delayTime)
                        if (!isStartVolPlusClick) continue
                        inputQuantity = BigDecimalUtils.add(inputQuantity, unit).toPlainString()
                        val msg = handler.obtainMessage()
                        msg.what = 3
                        msg.obj = inputQuantity
                        handler.sendMessage(msg)
                    }
                }.start()
                return@setOnTouchListener true
            }
            if (event.action == MotionEvent.ACTION_UP ||
                    event.action == MotionEvent.ACTION_CANCEL) {
                isVolumeLongClick = false
                isStartVolPlusClick = false
            }
            true
        }

    }


    private fun selectPriceType(position: Int) {
        tv_limit.background = null
        tv_limit.setTextColor(ContextCompat.getColor(context!!, R.color.colorTextLogin))
        tv_market.background = null
        tv_market.setTextColor(ContextCompat.getColor(context!!, R.color.colorTextLogin))
        ll_quantity.visibility = View.VISIBLE
        tv_market_price.visibility = View.VISIBLE
        inputQuantity = "0"
        inputPrice = "0"
        et_quantity.setText("")
        et_price.setText("")
        custom_seek_bar.progress = 0
        tv_all_quantity.text = "--"
        tv_all_transaction_money.text = "--"
        when (position) {
            0 -> {
                tv_market_price.visibility = View.GONE
                ll_all_transaction_money.visibility = View.VISIBLE
                priceType = TYPE_CUSTOM
                tv_market_price.visibility = View.GONE
                tv_limit.background = ContextCompat.getDrawable(context!!,R.drawable.bg_btn_gradient)
                tv_limit.setTextColor(Color.WHITE)
                if (transactionType == TYPE_BUY) {
                    et_price.hint = getString(R.string.buy_price)
                    et_quantity.hint = getString(R.string.buy_quantity)
                } else {
                    et_price.hint = getString(R.string.sell_price)
                    et_quantity.hint = getString(R.string.sell_quantity)
                    if (balanceData != null) {
                        tv_all_quantity.text = SymbolInterceptUtils.interceptData(balanceData!!.baseCoinBalance,
                                symbol.key, "volume") +
                                symbol.name.substring(0, symbol.name.indexOf("/"))
                    }
                }
            }
            1 -> {
                tv_market_price.visibility = View.VISIBLE
                ll_all_transaction_money.visibility = View.INVISIBLE
                priceType = TYPE_MARCKET
                ll_quantity.visibility = View.GONE
                tv_market.background = ContextCompat.getDrawable(context!!,R.drawable.bg_btn_gradient)
                tv_market.setTextColor(Color.WHITE)
                if (transactionType == TYPE_BUY) {
                    et_price.hint = getString(R.string.hint_buy_amount)
                    if (balanceData != null) {
                        tv_all_quantity.text = SymbolInterceptUtils.interceptData(balanceData!!.countCoinBalance,
                                symbol.key, "price") +
                                symbol.name.substring(symbol.name.indexOf("/") + 1)
                    }
                } else {
                    et_price.hint = getString(R.string.sell_quantity)
                    if (balanceData != null) {
                        tv_all_quantity.text = SymbolInterceptUtils.interceptData(balanceData!!.baseCoinBalance,
                                symbol.key, "volume") +
                                symbol.name.substring(0, symbol.name.indexOf("/"))
                    }
                }

            }
        }
        setAutoPrice()
    }

    private fun selectDeepType(position: Int) {
        tv_deep_one.background = null
        tv_deep_one.setTextColor(ContextCompat.getColor(context!!, R.color.colorTextLogin))
        tv_deep_two.background = null
        tv_deep_two.setTextColor(ContextCompat.getColor(context!!, R.color.colorTextLogin))
        tv_deep_three.background = null
        tv_deep_three.setTextColor(ContextCompat.getColor(context!!, R.color.colorTextLogin))
        clearDetailView()
        when (position) {
            0 -> {
                tv_deep_one.background = ContextCompat.getDrawable(context!!,R.drawable.bg_tab_top)
                tv_deep_one.setTextColor(Color.WHITE)
                subMessage(getDetailSendMsg(false))
                step = "0"
                subMessage(getDetailSendMsg())
            }
            1 -> {
                tv_deep_two.background = ContextCompat.getDrawable(context!!,R.drawable.bg_tab_top)
                tv_deep_two.setTextColor(Color.WHITE)
                subMessage(getDetailSendMsg(false))
                step = "1"
                subMessage(getDetailSendMsg())
            }
            2 -> {
                tv_deep_three.background = ContextCompat.getDrawable(context!!,R.drawable.bg_tab_top)
                tv_deep_three.setTextColor(Color.WHITE)
                subMessage(getDetailSendMsg(false))
                step = "2"
                subMessage(getDetailSendMsg())
            }
            else -> {
                tv_deep_one.setTextColor(Color.WHITE)
            }
        }
    }


    private lateinit var mSocketClient: WebSocketClient

    private fun initSocket() {
        mSocketClient = object : WebSocketClient(URI(ApiConstants.SOCKET_ADDRESS)) {
            override fun onOpen(handshakedata: ServerHandshake?) {
                Log.i("test", "onOpen")
                subMessage(getNewSendMsg())
                subMessage(getDetailSendMsg())
            }

            override fun onClose(code: Int, reason: String?, remote: Boolean) {
                Log.i("test", "onClose")
            }


            override fun onMessage(bytes: ByteBuffer?) {
                super.onMessage(bytes)
                handleData(GZIPUtils.uncompressToString(bytes?.array()))
            }

            override fun onMessage(message: String?) {
                Log.i("test", "onMessage")
            }

            override fun onError(ex: Exception?) {
                Log.i("test", "onError")
            }
        }
        wsConnect()
    }


    //连接socket
    fun wsConnect() {
        Thread(Runnable {
            mSocketClient.connect()
        }).start()
    }


    //发送信息
    private fun subMessage(msg: String) {
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
        val json = JSONObject(data)
        if (!json.isNull("tick")) {
            if (json.getString("channel") == newChannel) {
                val message = Message.obtain()
                message.what = 0
                message.obj = data
                handler.sendMessage(message)
            }
            if (json.getString("channel") == detailChannel) {
                val message = Message.obtain()
                message.what = 1
                message.obj = data
                handler.sendMessage(message)
            }
        }
    }

    fun getDetailSendMsg(isSub: Boolean = true): String {

        var name = symbol.key
        var event = if (isSub)
            "sub"
        else
            "unsub"
        detailChannel = "market_${name}_depth_step${step}"
        return "{\"event\":\"${event}\",\"params\":{\"channel\":\"${detailChannel}\",\"cb_id\":\"自定义\",\"asks\":5,\"bids\":5}}"
    }

    fun getNewSendMsg(isSub: Boolean = true): String {
        val event = if (isSub)
            "sub"
        else
            "unsub"
        newChannel = "market_${symbol.key}_trade_ticker"
        return "{\"event\":\"${event}\",\"params\":{\"channel\":\"${newChannel}\",\"cb_id\":\"自定义\"}}"
    }

    //更新实时页面
    fun refreshNewView(newTransactionData: NewTransactionData) {
        if (!newTransactionData.tick.data.isEmpty())
            tv_new_price.text = newTransactionData.tick.data[0].price + " " +
                    symbol.name.substring(symbol.name.indexOf("/") + 1)
    }

    //更新详情页面
    fun refreshDetailView(transactionData: TransactionData) {
        for (i in 0 until sellViewList.size) {
            Log.d("=========details::==","=====asks:=="+transactionData.toString())

            /**
             * 卖出
             */
            if (transactionData.tick.asks.size > sellViewList.size){
                /**
                 * 移除大值
                 */
                val subList = transactionData.tick.asks.subList(transactionData.tick.asks.size-sellViewList.size,transactionData.tick.asks.size)
                for (ii in subList){
                    Log.d(TAG,"======sub:::=="+ii.toString())
                }

                sellViewList[i].tv_price_item.text = SymbolInterceptUtils.interceptData(
                        subList[i].get(0).toString().replace("\"", "").trim(),
                        symbol.key, "price")
                sellViewList[i].tv_quantity_item.text = SymbolInterceptUtils.interceptData(
                        subList[i].get(1).toString(),
                        symbol.key, "volume")

            } else {

                val temp = sellViewList.size - transactionData.tick.asks.size
                sellViewList[i].tv_price_item.text = "--"
                sellViewList[i].tv_quantity_item.text = "--"
                if (i >= temp) {
                    sellViewList[i].tv_price_item.text = SymbolInterceptUtils.interceptData(
                            transactionData.tick.asks[i - temp].get(0).toString().replace("\"", "").trim(),
                            symbol.key, "price")
                    sellViewList[i].tv_quantity_item.text = SymbolInterceptUtils.interceptData(
                            transactionData.tick.asks[i - temp].get(1).toString(),
                            symbol.key, "volume")
                }

            }

            /**
             * 买入
             */
            if (transactionData.tick.buys.size > i) {
                buyViewList[i].tv_price_item.text = SymbolInterceptUtils.interceptData(transactionData.tick.buys[i].get(0).toString().replace("\"", "").trim(),
                        symbol.key, "price")

                buyViewList[i].tv_quantity_item.text = SymbolInterceptUtils.interceptData(transactionData.tick.buys[i].get(1).toString().trim(),
                        symbol.key, "volume")
            } else {
                buyViewList[i].tv_price_item.text = "--"
                buyViewList[i].tv_quantity_item.text = "--"
            }
        }
        setAutoPrice()
    }


    private fun setAutoPrice() {
        if (buyViewList.isEmpty()) return
        if (sellViewList.isEmpty()) return
        if (priceType == TYPE_CUSTOM && transactionType == TYPE_BUY) {
            if (TextUtils.isEmpty(et_price.text) &&
                    sellViewList.last().tv_price_item.text != "--") {
                et_price.setText(sellViewList.last().tv_price_item.text)
            }
        }
        if (priceType == TYPE_CUSTOM && transactionType == TYPE_SELL) {
            if (TextUtils.isEmpty(et_price.text) &&
                    buyViewList[0].tv_price_item.text != "--") {
                et_price.setText(buyViewList[0].tv_price_item.text)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ChooseSymbolActivity.CODE_CHOOSE_SYMBOL &&
                resultCode == RESULT_OK) {
            val symbol = data?.getParcelableExtra<SymbolData.Symbol>("symbol") ?: return
            if (symbol.key == this.symbol.key) return
            if (mSocketClient.isOpen) {
                subMessage(getDetailSendMsg(false))
                subMessage(getNewSendMsg(false))
                reset()
                clearDetailView()
                this.symbol.saveSymbol(symbol)
                tv_choose_symbol.text = symbol.name
                updateView()
            } else {
                reset()
                clearDetailView()
                this.symbol.saveSymbol(symbol)
                tv_choose_symbol.text = symbol.name
                initSocket()
            }
            onRefresh()
        }
    }


    override fun loadData() {
        enableRefresh(LoginManager.checkLogin(context, false))
        getEntrust()
    }

    //当前委托
    private fun getEntrust() {
        if (!LoginManager.checkLogin(this, false)) return
        HttpClient.instance.getNewEntrust(maxId, 40, symbol.key)
                .bindToLifecycle(this)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : NetObserver<EntrustData>() {
                    override fun onHandleSuccess(t: EntrustData?) {
                        if (t != null) {
                            Log.d(TAG, "=====ttttt===" + t.toString())

//                            if (t.count < mCount) {
//                                enableLoadMore(false)
//                            } else {
//                                enableLoadMore(true)
//                            }
                            maxId = t.max_id
                            mList.addAll(t.list)
                        }
                        setListAdapter()
                    }
                })
    }

    override fun onCreateLayoutManager(): RecyclerView.LayoutManager =
            FullyLinearLayoutManager(context)


    /**
     * 委托相关
     */

    override fun getView(parent: ViewGroup?, viewType: Int): View = layoutInflater.inflate(R.layout.item_list_entrust_new, parent, false)

    @SuppressLint("SetTextI18n")
    override fun bindDataToView(holder: FNAdapter.MyViewHolder?, position: Int) {
        holder!!.itemView.tv_type.text = mList[position].side_msg
        if (mList[position].side == "BUY") {
            holder.itemView.tv_type.setBackgroundResource(R.drawable.ic_transaction_buy)
        } else {
            holder.itemView.tv_type.setBackgroundResource(R.drawable.ic_transaction_sell)
        }
        /**
         * 委托的显示时间
         */
        holder.itemView.tv_date.text = com.fengniao.news.util.DateUtils.longToString("yyyy-MM-dd HH:mm:ss", mList[position].created_at)
        /**
         * 价格名字
         */
        holder.itemView.tv_price_name.text = mList[position].price.title + "(" +
                symbol.name.substring(symbol.name.indexOf("/") + 1).toUpperCase() + ")"
        /**
         * 价格
         */
        holder.itemView.tv_price.text = mList[position].price.amount

        /**
         * 委托量title
         */
        holder.itemView.tv_trust_quantity_name.text = mList[position].volume.title + "(" +
                symbol.name.substring(0, symbol.name.indexOf("/")).toUpperCase() + ")"
        /**
         * 委托量
         */

        Log.d(TAG, "=========volume:===" + mList[position].volume.amount)
        holder.itemView.tv_trust_quantity.text = mList[position].volume.amount


        if (!TextUtils.isEmpty(mList[position].deal_volume.title))
            holder.itemView.tv_deal_quantity_name.text = mList[position].deal_volume.title + "(" +
                    symbol.name.substring(0, symbol.name.indexOf("/")).toUpperCase() + ")"
        if (!TextUtils.isEmpty(mList[position].deal_volume.amount))
            holder.itemView.tv_deal_quantity.text = mList[position].deal_volume.amount
        holder.itemView.tv_transaction_status.text = mList[position].label.title
//        if (mList[position].label.click == 1) {
//            holder.itemView.iv_transaction_status.setImageResource(R.drawable.ic_undo)
//        } else {
//            holder.itemView.iv_transaction_status.setImageResource(R.drawable.ic_undo_no)
//        }


        /**
         * 撤单操作
         */
        holder.itemView.tv_transaction_status.setOnClickListener {
            if (mList[position].label.click == 1)
                deleteOrder(mList[position].id.toString(), position)
        }
    }

    override fun onItemClick(holder: FNAdapter.MyViewHolder?, position: Int) {
        val intent: Intent = Intent(context, EntrustDetailActivity::class.java)
        subMessage(getDetailSendMsg(false))
        subMessage(getNewSendMsg(false))
        intent.putExtra("id", mList[position].id)
        intent.putExtra("symbol", symbol.key)
        intent.putExtra("name", symbol.name)
        jumpToActivity(intent)
    }


    fun getBalance() {
        HttpClient.instance
                .getBalance(symbol.key)
                .bindToLifecycle(this)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : NetObserver<BalanceData>() {
                    override fun onHandleSuccess(t: BalanceData?) {
                        if (t != null) {
                            balanceData = t
//                            tv_new_price.text = balanceData?.price + symbol.name.substring(symbol.name.indexOf("/") + 1)
                            if (transactionType == TYPE_BUY) {
                                canUseMoney = t.countCoinBalance
                                tv_total_money.text = t.countCoinBalance + " " + symbol.name.substring(symbol.name.indexOf("/") + 1)
                                if (priceType == TYPE_MARCKET) {
                                    tv_all_quantity.text = SymbolInterceptUtils.interceptData(canUseMoney, symbol.key, "price") +
                                            symbol.name.substring(symbol.name.indexOf("/") + 1)
                                }
                            } else {
                                canUseMoney = t.baseCoinBalance
                                tv_total_money.text = t.baseCoinBalance + " " + symbol.name.substring(0, symbol.name.indexOf("/"))
                                tv_all_quantity.text = SymbolInterceptUtils.interceptData(canUseMoney, symbol.key, "volume") +
                                        symbol.name.substring(0, symbol.name.indexOf("/"))
                            }
                        }
                    }
                })
    }


    fun getMyAssets() {
        HttpClient.instance.getAssets()
                .bindToLifecycle(this)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : NetObserver<AssetsData>() {
                    override fun onHandleSuccess(t: AssetsData?) {
                        if (t != null) {
                            for (i in 0 until t.list.size) {
                                if (transactionType == TYPE_BUY) {
                                    if (symbol.key.endsWith(t.list[i].coin)) {
                                        mAssets = t.list[i]
                                        canUseMoney = t.list[i].normal.amount
                                        tv_total_money.text = t.list[i].normal.amount + " " + t.list[i].coin.toUpperCase()
                                        if (priceType == TYPE_MARCKET) {
                                            tv_all_quantity.text = t.list[i].coin.toUpperCase() + SymbolInterceptUtils.interceptData(canUseMoney,
                                                    symbol.key, "price")
                                        }
                                        break
                                    }
                                } else {
                                    if (symbol.key.startsWith(t.list[i].coin)) {
                                        mAssets = t.list[i]
                                        canUseMoney = t.list[i].normal.amount
                                        tv_total_money.text = t.list[i].normal.amount + " " + t.list[i].coin.toUpperCase()
                                        tv_all_quantity.text = SymbolInterceptUtils.interceptData(canUseMoney, symbol.key, "volume") +
                                                symbol.name.substring(0, symbol.name.indexOf("/"))
                                        break
                                    }
                                }
                            }
                        }
                    }
                })
    }

    /**
     * 下单
     */
    fun createOrder(capital_pword: String) {
        val side = if (transactionType == TYPE_BUY) {
            "BUY"
        } else {
            "SELL"
        }
        val type = if (priceType == TYPE_CUSTOM) {
            1
        } else {
            2
        }
        val volume = if (priceType == TYPE_CUSTOM) {
            //表示买卖数量
            inputQuantity

        } else {
            //买则表示总价格，卖表示总个数
            inputPrice
        }
        //限价模式下表示价格，市价无意义
        val price = inputPrice
        HttpClient.instance
                .createOrder(side, type, volume, capital_pword, price, symbol.key)
                .bindToLifecycle(this)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : NetObserver<JsonObject>() {
                    override fun onHandleSuccess(t: JsonObject?) {
//                        //更新资金信息
//                        updateSymbolInfo()
//                        //刷新委托列表
//                        onRefresh()
//                        val user = LoginManager.getInstance().user
//                        if (user != null && user.user.exchange_verify == 2) {
//                            LoginManager.getInstance().saveAssetsTime(
//                                    System.currentTimeMillis()
//                            )
//                        }
//                        //判断fragmentdialog是否已经显示
//                        if (assetCertDialog != null && assetCertDialog!!.dialog != null
//                                && assetCertDialog!!.dialog.isShowing) {
//                            assetCertDialog?.dismiss()
//                        }

                    }

                    override fun onHandleSuccess(t: JsonObject?, msg: String?) {
                        super.onHandleSuccess(t, msg)

                        Log.d(TAG, "------" + msg)


                        //更新资金信息
                        updateSymbolInfo()
                        //刷新委托列表
                        onRefresh()
                        val user = LoginManager.getInstance().user
                        if (user != null && user.user.exchange_verify == 2) {
                            LoginManager.getInstance().saveAssetsTime(
                                    System.currentTimeMillis()
                            )
                        }
                        //判断fragmentdialog是否已经显示
                        if (assetCertDialog != null && assetCertDialog!!.dialog != null
                                && assetCertDialog!!.dialog.isShowing) {
                            assetCertDialog?.dismiss()
                        }
                        reset()
                        UIUtils.showToast(getString(R.string.toast_trade_success))
                    }
                })
    }

    fun deleteOrder(order_id: String, position: Int) {
        showProgressDialog("")
        HttpClient.instance
                .deleteOrder(order_id, symbol.key)
                .bindToLifecycle(this)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : NetObserver<String>() {
                    override fun onHandleSuccess(t: String?) {
                    }

                    override fun onHandleSuccess(t: String?, msg: String?) {
                        super.onHandleSuccess(t, msg)
                        Log.d("====test====", "====sfsfsff====" + msg)
                        UIUtils.showToast(msg)
                        cancelProgressDialog()
//                        onRefresh()
                        mList.removeAt(position)
                        notifyDataSetChanged()
                    }

                    override fun onHandleError(msg: String?) {
                        super.onHandleError(msg)
                        cancelProgressDialog()
                    }
                })
    }


    //获取价格变化单位
    fun getMoneyUnit(): String {
        if (balanceData != null) {
            priceScale = balanceData!!.minPrice.replace("0.", "").length
            return balanceData!!.minPrice
        }
//        val rule: SymbolInterceptUtils.Rule? = SymbolInterceptUtils.symbolRules[symbol.key]
//        if (rule != null) {
//            priceScale = rule.priceLength
//            return BigDecimal.valueOf(1.toLong(), priceScale).toPlainString()
//        }
        return ""
    }


    //获取数量变化单位
    fun getVolumeUnit(): String {
        if (balanceData != null) {
            volumeScale = balanceData!!.minVolume.replace("0.", "").length
            Log.d(TAG, "===volumeScale:====" + volumeScale + ",==balanceData:=" + balanceData!!.minVolume)
            return balanceData!!.minVolume
        }
//        val rule: SymbolInterceptUtils.Rule? = SymbolInterceptUtils.symbolRules[symbol.key]
//        if (rule != null) {
//            volumeScale = rule.volumeLength
//            return BigDecimal.valueOf(1.toLong(), volumeScale).toPlainString()
//        }
        return ""
    }

    override fun onPause() {
        super.onPause()
        isVolumeLongClick = false
        isPriceLongClick = false
    }


    override fun onDestroy() {
        super.onDestroy()
        mSocketClient.close()
    }
}
