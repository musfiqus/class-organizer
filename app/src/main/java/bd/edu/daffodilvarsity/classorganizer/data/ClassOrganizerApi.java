package bd.edu.daffodilvarsity.classorganizer.data;

import bd.edu.daffodilvarsity.classorganizer.model.Database;
import bd.edu.daffodilvarsity.classorganizer.model.UpdateResponse;
import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Headers;

public interface ClassOrganizerApi {
    @Headers("Content-Type: application/json")
    @GET("stable.json")
    Single<UpdateResponse> getUpdate();

    @Headers("Content-Type: application/json")
    @GET("routine.json")
    Single<Database> getRoutine();
}
