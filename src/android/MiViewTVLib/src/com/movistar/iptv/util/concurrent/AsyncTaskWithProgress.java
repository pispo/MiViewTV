package com.movistar.iptv.util.concurrent;

public abstract class AsyncTaskWithProgress<T>
        extends android.os.AsyncTask<Void, T, Boolean>
        implements AsyncTaskProgressCallback<T> {

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

    @Override
    protected final void onProgressUpdate(T... status) {
        onProgressUpdate(status[0]);
    }

    protected abstract Boolean doInBackground();
    protected abstract void onProgressUpdate(T status);
    protected abstract void onSuccessResult();
    protected abstract void onFailureResult();

    @Override
    public final void publishProgress(T status) {
        super.publishProgress(status);
    }
}