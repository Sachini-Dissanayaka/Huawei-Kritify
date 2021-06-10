package com.huawei.kritify.retrofit;

import com.huawei.kritify.model.Post;
import com.huawei.kritify.model.Site;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface RetrofitInterface {

    @GET("/allPosts")
    Call<List<Post>> getAllPosts();

    @GET("/posts/{type}")
    Call<List<Post>> getPostsBySiteType(
            @Path("type") String type
    );

    @GET("/postsBySite/{siteId}")
    Call<List<Post>> getPostsBySite(
            @Path("siteId") long siteId
    );

    @GET("/sitesByTypeAndName/{type}/{name}")
    Call<List<Site>> getSitesByTypeAndName(
            @Path("type") String type,
            @Path("name") String name
    );

    @GET("/sitesByName/{name}")
    Call<List<Site>> getSitesByName(
            @Path("name") String name
    );

    @POST("/createSite")
    Call<Void> createSite(
            @Body Site site
    );

    @POST("/createPost")
    Call<Void> createPost(
            @Body Post post
    );

    @GET("/postsByToken/{userToken}")
    Call<List<Post>> getPostsByToken(
            @Path("userToken") String userToken
    );

    @GET("/postsByTokenAndType/{userToken}/{siteId}")
    Call<List<Post>> getPostsByTokenAndSiteId(
            @Path("userToken") String userToken,
            @Path("siteId") long siteId
    );

}

