package com.movistar.iptv.util.concurrent;

public interface AsyncTaskProgressCallback<T> {
    void publishProgress(T status);
}