package com.huawei.kritify.retrofit;

import com.huawei.kritify.model.Post;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface RetrofitInterface {

    @GET("/allPosts")
    Call<List<Post>> getAllPosts();

    @GET("/posts/{type}")
    Call<List<Post>> getPostsBySiteType(
            @Path("type") String type
    );

}

