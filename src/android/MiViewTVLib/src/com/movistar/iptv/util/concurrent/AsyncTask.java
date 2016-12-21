package com.movistar.iptv.util.concurrent;

public abstract class AsyncTask extends android.os.AsyncTask<Void, Void, Boolean> {

    @Override
    protected final Boolean doInBackground(Void... params) {
        return doInBackground();
    }

    @Override
    protected final void onPostExecute(final Boolean result) {
        if (result)
            onSuccessResult();
        else
            onFailureResult();
    }

    protected abstract Boolean doInBackground();
    protected abstract void onSuccessResult();
    protected abstract void onFailureResult();
}
