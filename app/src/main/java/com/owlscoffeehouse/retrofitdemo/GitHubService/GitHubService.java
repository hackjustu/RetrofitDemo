package com.owlscoffeehouse.retrofitdemo.GitHubService;


import com.google.gson.JsonElement;
import com.owlscoffeehouse.retrofitdemo.GitHubService.Model.Contributor;

import java.util.List;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Path;

public interface GitHubService {

    @GET("/repos/{owner}/{repo}/contributors")
    Call<List<Contributor>> contributors(
            @Path("owner") String owner,
            @Path("repo") String repo);

    @GET("/repos/{owner}/{repo}/contributors")
    Call<JsonElement> contributorsRaw(
            @Path("owner") String owner,
            @Path("repo") String repo);
}
