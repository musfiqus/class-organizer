package bd.edu.daffodilvarsity.classorganizer.utils;

import bd.edu.daffodilvarsity.classorganizer.data.UpdateResponse;
import io.reactivex.Single;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface ClassOrganizerApi {
    @Headers("Content-Type: application/json")
    @GET("update.json")
    Single<UpdateResponse> getUpdate();

    @Streaming
    @GET
    Call<ResponseBody> downloadFile(@Url String fileUrl);
}
