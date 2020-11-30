package com.freshlybuilt.Fragments


import android.os.Bundle
import android.os.Handler
import android.os.Parcelable
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.freshlybuilt.API.FeedFetchMethod
import com.freshlybuilt.Adapters.FeedAdapter
import com.freshlybuilt.Modals.FeedItem

import com.freshlybuilt.R
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.fragment_feed.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.lang.Exception

class Feed : Fragment(R.layout.fragment_feed) {

    var isScrolling              = false
    var page           : Int     = 1
    var nextSetLoading : Boolean = false

    val layoutManager                = LinearLayoutManager(activity,LinearLayoutManager.HORIZONTAL,false)
    val API_CALL_FETCH_POST : String = "https://freshlybuilt.com/api/get_recent_posts/?page="

    lateinit var fetchData     : ArrayList<FeedItem>
    lateinit var feedRecycler  : RecyclerView
    lateinit var adapter       : FeedAdapter
    lateinit var requestQueue  : RequestQueue
    lateinit var method        : FeedFetchMethod
    lateinit var responseFetch : JSONObject


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        requestQueue = Volley.newRequestQueue(activity)
        method = FeedFetchMethod()
        feedRecycler = feed_recycler
        pageRequest()
        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(feedRecycler)
        feedRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener(){

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState ==AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                    isScrolling = true
                }
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE){
                    isScrolling = false
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val currentItems = layoutManager.childCount
                val totalItems = layoutManager.itemCount
                val scrollOutItems = layoutManager.findFirstVisibleItemPosition()
                val recyclerViewState = feedRecycler.layoutManager?.onSaveInstanceState()
                Log.d("recycler","current : ${currentItems.toString()} scrolled : ${scrollOutItems.toString()} total : ${totalItems.toString()}")

                if ( isScrolling && (currentItems + scrollOutItems >= totalItems)){
                    if (!nextSetLoading) {
                        pageRequest()
                        feedRecycler.layoutManager?.onRestoreInstanceState(recyclerViewState)
                        isScrolling =false
                    }

                    Log.d("Scroll","onScrolled Loading being called")
                }
            }
        })
    }


    private fun fetchPostCall(response : JSONObject): ArrayList<FeedItem>{
        val data = ArrayList<FeedItem>()
        val fetchedPostArray = response.getJSONArray("posts")
        var imageUrl : String
        for (i in 0 until fetchedPostArray.length()){
            try{
                val post = fetchedPostArray.getJSONObject(i)
                val image : Int = R.drawable.default_image

                val author : JSONObject = post.getJSONObject("author")
                val date : String = post.getString("date")
                val description : String = post.getString("excerpt")
                val postId : Int = post.getInt("id")
                val title : String = post.getString("title")
                val content : String = post.getString("content")
                val author_id : Int = author.getInt("id")
                val author_nickname : String = author.getString("nickname")
                val author_fullname : String = author.getString("first_name")+" "+author.getString("last_name")


                try{
                    val attachments : JSONArray = post.getJSONArray("attachments")
                    val zerothAttachmentObject = attachments.getJSONObject(0)
                    imageUrl = zerothAttachmentObject.getString("url")
                    Log.d("url",imageUrl)
                }catch (e : Exception){
                    Log.d("attachments", e.toString())
                    imageUrl = "default"
                }
                val dataPerPost = FeedItem(image,imageUrl ,author_fullname,date,description,postId,title,content,author_id,author_nickname)

                data.add(dataPerPost)
            }catch (e : Exception){
                Log.d("fetchPostCall",e.toString())
            }
        }
        return data
    }

    private fun pageRequest(){
        nextSetLoading = true
        loadingProgressAnim()
        val requestPostFetch : StringRequest = StringRequest(Request.Method.GET, API_CALL_FETCH_POST+this.page.toString(),
            Response.Listener<String> { response ->
                try{
                    val responseJson : JSONObject = JSONObject(response)
                    if (responseJson["status"]=="ok"){
                        Log.d("fetch","success")
                        responseFetch = JSONObject(response)
                        if (this::adapter.isInitialized){
                            this.fetchData.addAll(fetchPostCall(responseFetch))
                            adapter.notifyDataSetChanged()
                            nextSetLoading = false
                            loadingProgressAnim()
                            Log.d("else","if called")
                        }else{
                            Log.d("else","else called")
                            this.fetchData = fetchPostCall(responseFetch)
                            adapter = FeedAdapter(fetchData)
                            feedRecycler.adapter = adapter
                            feedRecycler.layoutManager = layoutManager
                            nextSetLoading = false
                            loadingProgressAnim()
                        }
                    }
                    else{
                        Log.d("fetch","failed")
                    }
                }catch (e : JSONException) {
                    Log.d("fetch","exception")
                }
            }, Response.ErrorListener {
                Log.d("fetch","fetch error")
            })
        Log.d("page",this.page.toString())
        this.page+=1

        requestQueue.add(requestPostFetch)
        }

    private fun loadingProgressAnim(){
        if (post_fetch_in_progress.visibility == View.GONE){
            TransitionManager.beginDelayedTransition(loading_card, AutoTransition())
            post_fetch_in_progress.visibility = View.VISIBLE
        }else{
            TransitionManager.beginDelayedTransition(loading_card, AutoTransition())
            post_fetch_in_progress.visibility = View.GONE
        }
    }

}
