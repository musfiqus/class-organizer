package bd.edu.daffodilvarsity.classorganizer.data;

import bd.edu.daffodilvarsity.classorganizer.model.Database;
import bd.edu.daffodilvarsity.classorganizer.model.UpdateResponse;
import io.reactivex.Single;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface ClassOrganizerApi {
    @Headers("Content-Type: application/json")
    @GET("stable.json")
    Single<UpdateResponse> getUpdate();

    @Headers("Content-Type: application/json")
    @GET("routine.json")
    Single<Database> getRoutine();
}
