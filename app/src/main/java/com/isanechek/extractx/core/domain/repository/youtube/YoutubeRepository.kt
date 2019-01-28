package com.isanechek.extractx.core.domain.repository.youtube

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.isanechek.extractx.core.common.DebugUtilsContract
import com.isanechek.extractx.core.common.Mapper
import com.isanechek.extractx.core.data.network.WorkerHelperContract
import com.isanechek.extractx.core.data.youtube.YoutubeParserContract
import com.isanechek.extractx.core.data.youtube.dto.YoutubeItem
import com.isanechek.extractx.core.domain.models.DashboardModel
import com.isanechek.extractx.core.domain.models.Request
import com.isanechek.extractx.core.domain.models.Resource
import com.isanechek.extractx.core.domain.models.Response
import com.isanechek.extractx.core.domain.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class YoutubeRepository(
    private val debug: DebugUtilsContract,
    private val workerHelper: WorkerHelperContract,
    private val parser: YoutubeParserContract,
    private val mapper: Mapper<DashboardModel, YoutubeItem>
) : Repository<DashboardModel> {

    private val resource = MutableLiveData<Resource<DashboardModel>>()

    override fun resource(): LiveData<Resource<DashboardModel>> = resource

    override suspend fun loadData(contentUrl: String) = withContext(Dispatchers.Default) {
        debug.log("$TAG load data")
        resource.postValue(Resource.Loading)
        val requestUrl = parser.getVideoId(contentUrl)
        if (requestUrl.isEmpty()) {
            debug.log("$TAG empty request url!")
            resource.postValue(Resource.Error("Bad request url!"))
            return@withContext
        }
        debug.log("$TAG request url $requestUrl")
        val response = workerHelper.fetchFromServer(Request.Youtube(requestUrl))
        when(response) {
            is Response.YoutubeResult -> {
                debug.log("$TAG Result Ok!")
                val item = parser.parse(response.content)
                val result = mapper.map(item)
                resource.postValue(Resource.Success(result))
            }
            is Response.Empty -> {
                debug.log("$TAG Response Empty!")
            }
            is Response.NetworkError -> {
                debug.log("$TAG Network error!")
            }
            is Response.RequestError -> {
                debug.log("$TAG Request error!")
            }
            is Response.ServerError -> {
                debug.log("$TAG Server error!!! Code -> ${response.errorCode}")
            }
        }
    }

    companion object {
        private const val TAG = "YoutubeRepository"
    }

}