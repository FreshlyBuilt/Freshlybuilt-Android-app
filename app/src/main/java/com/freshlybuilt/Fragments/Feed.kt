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
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.freshlybuilt.API.FeedFetchMethod
import com.freshlybuilt.Adapters.FeedAdapter
import com.freshlybuilt.Modals.FeedItem

import com.freshlybuilt.R
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.fragment_feed.*
import org.json.JSONObject
import java.lang.Exception

class Feed : Fragment(R.layout.fragment_feed) {

    var isScrolling = false
    var page : Int = 0

    val layoutManager = LinearLayoutManager(activity,LinearLayoutManager.HORIZONTAL,false)

    lateinit var fetchData    : ArrayList<FeedItem>
    lateinit var feedRecycler : RecyclerView
    lateinit var adapter      : FeedAdapter
    lateinit var requestQueue : RequestQueue
    lateinit var method       : FeedFetchMethod

    var nextSetLoading : Boolean = false

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        requestQueue = Volley.newRequestQueue(activity)
        method = FeedFetchMethod()
        feedRecycler = feed_recycler
        loadingProgressAnim()
        requestQueue.add(method.PageFetch(page))
        requestQueue.addRequestFinishedListener<StringRequest> {
            try{
                fetchData = fetchPostCall(method.responseFetch)
            }catch (e:Exception){
                Log.d("first",e.toString())
            }
            adapter = FeedAdapter(fetchData)
            feedRecycler.adapter = adapter
            feedRecycler.layoutManager = layoutManager
            loadingProgressAnim()
            Log.d("array",fetchData.size.toString())
        }

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
                        loadMoreData()
                        feedRecycler.layoutManager?.onRestoreInstanceState(recyclerViewState)
                        isScrolling =false
                    }

                    Log.d("Scroll","onScrolled Loading being called")
                }
            }
        })
    }

    private fun loadMoreData(){
        nextSetLoading = true
        loadingProgressAnim()
        page+=1
        requestQueue.add(method.PageFetch(page))
        requestQueue.addRequestFinishedListener<StringRequest> {
            fetchData.addAll(fetchPostCall(method.responseFetch))
            val adapterVar = feedRecycler.adapter
            adapter.notifyItemInserted(fetchData.size-1)
            nextSetLoading = false
            loadingProgressAnim()
            Log.d("array",fetchData.size.toString())
        }
    }

    private fun fetchPostCall(response : JSONObject): ArrayList<FeedItem>{
        val data = ArrayList<FeedItem>()
        val fetchedPostArray = response.getJSONArray("posts")
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
                val dataPerPost = FeedItem(image,author_fullname,date,description,postId,title,content,author_id,author_nickname)
                data.add(dataPerPost)
            }catch (e : Exception){
                Log.d("fetchPostCall",e.toString())
            }

        }
        return data
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
