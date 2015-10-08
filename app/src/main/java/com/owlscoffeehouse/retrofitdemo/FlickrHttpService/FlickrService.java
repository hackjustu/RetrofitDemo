package com.owlscoffeehouse.retrofitdemo.FlickrHttpService;

import com.owlscoffeehouse.retrofitdemo.FlickrHttpService.Model.ItemHolder;

import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.http.GET;
import retrofit.http.Query;

public interface FlickrService {

    @GET("/services/feeds/photos_public.gne")
    Call<ItemHolder> getSyncImage(
            @Query("tags") String tags,
            @Query("tagmode") String tagmode,
            @Query("format") String format,
            @Query("nojsoncallback") String nojsoncallback);

    @GET("/services/feeds/photos_public.gne?tagmode=all&format=json&nojsoncallback=1")
    Call<ItemHolder> getSyncImage(
            @Query("tags") String tags);

}
