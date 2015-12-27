package info.fandroid.navdrawer.fragments;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Calendar;

import info.fandroid.navdrawer.DetailActivity;
import info.fandroid.navdrawer.GalleryAdapter;
import info.fandroid.navdrawer.ImageModel;
import info.fandroid.navdrawer.R;
import info.fandroid.navdrawer.RecyclerItemClickListener;
import info.fandroid.navdrawer.retrogram.Instagram;
import info.fandroid.navdrawer.retrogram.model.Media;
import info.fandroid.navdrawer.retrogram.model.SearchMediaResponse;
import info.fandroid.navdrawer.util.GPSTracker;
import info.fandroid.navdrawer.util.LocationParams;
import info.fandroid.navdrawer.vk.VK;
import info.fandroid.navdrawer.vk.model.Items;
import info.fandroid.navdrawer.vk.model.VkResponse;
import retrofit.RestAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentGallery.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentGallery#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentGallery extends Fragment {

    GalleryAdapter mAdapter;

    ArrayList<ImageModel> data = new ArrayList<>();

    protected static String AccessToken = "398315918.19f142f.1a6004bc7ce04dc1bc0a7914095a30cb";
    protected static String ClientId = "19f142f8b92641e7b528497c9d206379";
    protected ArrayList<ImageModel> imagesList = new ArrayList<>();

    protected Double latitude;
    protected Double longitude;

    protected Instagram instagram;

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

    // Declaring a Location Manager
    protected LocationManager locationManager;

    private static ProgressBar spinner;
    // TODO: Rename and change types of parameters
    private RecyclerView recyclerView;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentGallery.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentGallery newInstance(String param1, String param2) {
        FragmentGallery fragment = new FragmentGallery();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public FragmentGallery() {
        // Required empty public constructor
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_gallery, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.list);

        latitude = ((LocationParams)getActivity().getApplication()).getLatitude();
        longitude = ((LocationParams) getActivity().getApplication()).getLongitude();
        Log.i("COORDINATES : {} }",  String.valueOf(latitude)  + " - "+ String.valueOf(longitude));
        recyclerView.setLayoutManager(new GridLayoutManager(rootView.getContext(), 3));
        recyclerView.setHasFixedSize(true);
//
//
        mAdapter = new GalleryAdapter(rootView.getContext(), data);
        mAdapter.clearData();
        recyclerView.setAdapter(mAdapter);
        spinner = (ProgressBar) rootView.findViewById(R.id.loading);
        new AsyncHttpTask().execute();

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(),
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        startImagePagerActivity(position);
                    }
                }));
        return rootView;
    }

    private void startImagePagerActivity(int position) {
        Intent intent = new Intent(getActivity(), DetailActivity.class);
        intent.putParcelableArrayListExtra("data", data);
        intent.putExtra("pos", position);
        startActivity(intent);
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }


    //Downloading data asynchronously
    public class AsyncHttpTask extends AsyncTask<String, Void, Integer> {

        protected void onPreExecute (){
            spinner.setVisibility(View.VISIBLE);
        }

        @Override
        protected Integer doInBackground(String... params) {

            Integer result = 0;
            try {
                instagram = new Instagram(AccessToken, RestAdapter.LogLevel.BASIC);
                long startTime = System.currentTimeMillis() / 1000L;
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.WEEK_OF_YEAR, -7);
                long endTime = calendar.getTimeInMillis() / 1000L;
                final SearchMediaResponse response = instagram.getMediaEndpoint().search(5000, longitude, latitude, endTime, startTime);
                VK vk = new VK(RestAdapter.LogLevel.FULL);
                VkResponse resp = vk.getUsersEndpoint().search(latitude, longitude, 1000, 5000, startTime, endTime, 5.40);
                parseResultVk(resp);
                parseResult(response);
                result = 1; // Successful
            } catch (Exception e) {
                String msg = (e.getMessage()==null)?"Login failed!":e.getMessage();
                Log.i("error", msg);
                result = 0; //"Failed
            }

            return result;
        }

        @Override
        protected void onPostExecute(Integer result) {
            // Download complete. Lets update UI

            if (result == 1) {
                int counter = 0;
                for (ImageModel model : imagesList) {
                    ImageModel imageModel = new ImageModel();
                    imageModel.setName("Image " + model.getName());
                    imageModel.setUrl(model.getUrl());
                    data.add(imageModel);
                    counter++;
                }
                mAdapter.setImageData(data);
            } else {
				Toast.makeText(getActivity(), "Failed1 to fetch data!", Toast.LENGTH_SHORT).show();
            }
            //Hide progressbar
		spinner.setVisibility(View.GONE);
        }
    }

    /**
     * Parsing the feed results and get the list
     *
     * @param popular
     */
    private void parseResult(SearchMediaResponse popular) {
        if (popular.getMediaList() != null) {
            for (Media media : popular.getMediaList()) {
//                Log.i("link:", media.getImages().getLowResolution().getUrl());
                ImageModel model = new ImageModel();
                model.setUrl(media.getImages().getStandardResolution().getUrl());
                model.setName(media.getUser().getFullName());
                imagesList.add(model);
            }
        }
    }

    /**
     * Parsing the feed results and get the list
     *
     * @param popular
     */
    private  void parseResultVk(VkResponse popular) {
        if (popular.getResponse().getItems() != null) {
            for (Items media : popular.getResponse().getItems()) {
//                Log.i("link:", media.getPhoto_130());
                ImageModel model = new ImageModel();
                model.setUrl(media.getPhoto_604());
                model.setName("vk.com/id" + media.getOwner_id());
                imagesList.add(model);
            }
        }
    }
}
