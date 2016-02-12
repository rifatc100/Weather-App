package vandy.mooc.model;

import java.lang.ref.WeakReference;
import java.util.List;

import vandy.mooc.MVP;
import vandy.mooc.common.GenericServiceConnection;
import vandy.mooc.model.aidl.WeatherCall;
import vandy.mooc.model.aidl.WeatherData;
import vandy.mooc.model.aidl.WeatherRequest;
import vandy.mooc.model.aidl.WeatherResults;
import vandy.mooc.model.services.WeatherServiceAsync;
import vandy.mooc.model.services.WeatherServiceSync;
import android.content.Context;
import android.os.RemoteException;
import android.util.Log;

/**
 * This class plays the "Model" role in the Model-View-Presenter (MVP)
 * pattern by defining an interface for providing data that will be
 * acted upon by the "Presenter" and "View" layers in the MVP pattern.
 * It implements the MVP.ProvidedModelOps so it can be created/managed
 * by the GenericPresenter framework.
 */
public class WeatherModel
       implements MVP.ProvidedModelOps {
    /**
     * Debugging tag used by the Android logger.
     */
    protected final static String TAG = 
        WeatherModel.class.getSimpleName();

    /**
     * A WeakReference used to access methods in the Presenter layer.
     * The WeakReference enables garbage collection.
     */
    protected WeakReference<MVP.RequiredPresenterOps> mPresenter;

    /**
     * Location we're trying to get current weather for.
     */
    private String mLocation;

    // TODO -- define ServiceConnetions to connect to the
    // WeatherServiceSync and WeatherServiceAsync.

    private GenericServiceConnection<WeatherCall> weatherServiceConnectionSync;

    private GenericServiceConnection<WeatherRequest> weatherServiceConnectionAsync;

    /**
     * Hook method called when a new WeatherModel instance is created
     * to initialize the ServicConnections and bind to the WeatherService*.
     * 
     * @param presenter
     *            A reference to the Presenter layer.
     */
    @Override
    public void onCreate(MVP.RequiredPresenterOps presenter) {
        // Set the WeakReference.
        mPresenter = new WeakReference<>(presenter);

        // TODO -- you fill in here to initialize the WeatherService*.
        weatherServiceConnectionSync =
                new GenericServiceConnection<WeatherCall>(WeatherCall.class);

        weatherServiceConnectionAsync =
                new GenericServiceConnection<WeatherRequest>(WeatherRequest.class);

        // Bind to the services.
        bindService();
    }

    /**
     * Hook method called to shutdown the Presenter layer.
     */
    @Override
    public void onDestroy(boolean isChangingConfigurations) {
        // Don't bother unbinding the service if we're simply changing
        // configurations.
        if (isChangingConfigurations)
            Log.d(TAG,
                  "Simply changing configurations, no need to destroy the Service");
        else
            unbindService();
    }

    /**
     * The implementation of the WeatherResults AIDL Interface, which
     * will be passed to the Weather Web service using the
     * WeatherRequest.getCurrentWeather() method.
     * 
     * This implementation of WeatherResults.Stub plays the role of
     * Invoker in the Broker Pattern since it dispatches the upcall to
     * sendResults().
     */
    private final WeatherResults.Stub mWeatherResults =
        new WeatherResults.Stub() {
            /**
             * This method is invoked by the WeatherServiceAsync to
             * return the results back.
             */
            @Override
            public void sendResults(final WeatherData weatherResults)
                throws RemoteException {
                // Pass the results back to the Presenter's
                // displayResults() method.
                // TODO -- you fill in here.
                mPresenter.get().displayResults(weatherResults, "No Weather data for location \""
                                                + mLocation + "found");
                mLocation = null;
            }

            /**
             * This method is invoked by the WeatherServiceAsync to
             * return error results back.
             */
            @Override
            public void sendError(final String reason)
                throws RemoteException {
                // Pass the results back to the Presenter's
                // displayResults() method.
                // TODO -- you fill in here.
                mPresenter.get().displayResults(null, reason);
                mLocation = null;
            }
	};

    /**
     * Initiate the service binding protocol.
     */
    private void bindService() {
        Log.d(TAG,
              "calling bindService()");

        // Launch the Weather Bound Services if they aren't already
        // running via a call to bindService(), which binds this
        // activity to the WeatherService* if they aren't already
        // bound.

        // TODO -- you fill in here.
        if (weatherServiceConnectionSync.getInterface() == null) {
            mPresenter.get().getApplicationContext().bindService
                    (WeatherServiceSync.makeIntent(mPresenter.get().getActivityContext()),
                            weatherServiceConnectionSync, Context.BIND_AUTO_CREATE);
        }

        if(weatherServiceConnectionAsync.getInterface() == null) {
            mPresenter.get().getApplicationContext().bindService
                    (WeatherServiceAsync.makeIntent(mPresenter.get().getActivityContext()),
                            weatherServiceConnectionAsync, Context.BIND_AUTO_CREATE);
        }

    }

    /**
     * Initiate the service unbinding protocol.
     */
    private void unbindService() {
        Log.d(TAG,
              "calling unbindService()");

        // TODO -- you fill in here to unbind from the WeatherService*.
        // Unbind the Async Service if it is connected.
        if(weatherServiceConnectionAsync.getInterface() != null)
        {
            mPresenter.get().getApplicationContext().unbindService(weatherServiceConnectionAsync);
        }

        // Unbind the Sync Service if it is connected.
        if(weatherServiceConnectionSync.getInterface() != null)
        {
            mPresenter.get().getApplicationContext().unbindService(weatherServiceConnectionSync);
        }
    }

    /**
     * Initiate the asynchronous weather lookup.
     */
    public boolean getWeatherAsync(String location) {
        // TODO -- you fill in here.

        final WeatherRequest weatherRequest = weatherServiceConnectionAsync.getInterface();

        if(mLocation != null)
        {
            return false;
        }

        mLocation = location;
        if(weatherRequest != null){
            try {
                weatherRequest.getCurrentWeather(location, mWeatherResults);
            } catch (RemoteException e) {
                Log.e(TAG, "RemoteException " + e.getMessage());
                return false;
            }
        }
        else{
            Log.d(TAG, "weatherRequest was null.");
            return false;
        }

        return true;
    }

    /**
     * Initiate the synchronous weather lookup.
     */
    public WeatherData getWeatherSync(String location) {
        // TODO -- you fill in here.
        WeatherData wData = null;
        try {
            List<WeatherData> weatherDataList = weatherServiceConnectionSync.getInterface().getCurrentWeather(location);
            if(weatherDataList != null && !weatherDataList.isEmpty())
            {
                wData = weatherServiceConnectionSync.getInterface().getCurrentWeather(location).get(0);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return wData;
    }
}
