package io.pavan.movieapp.arch.mvp;

/**
 * Created by pavan on 01/12/18
 */
public class NullPresenter extends Presenter<IView> {

    public NullPresenter(IView view) {
        super(view);
    }
}
