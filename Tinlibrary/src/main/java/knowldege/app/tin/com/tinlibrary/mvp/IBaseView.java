package knowldege.app.tin.com.tinlibrary.mvp;
public interface IBaseView<T> {


    void setPersenter(T t);

    T bindPersenter();


}