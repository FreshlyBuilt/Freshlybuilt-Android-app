package com.freshlybuilt.Fragments


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.freshlybuilt.API.FeedFetchMethod
import com.freshlybuilt.Adapters.FeedAdapter
import com.freshlybuilt.Modals.FeedItem

import com.freshlybuilt.R
import kotlinx.android.synthetic.main.fragment_feed.*
import org.json.JSONObject
import java.lang.Exception

class Feed : Fragment(R.layout.fragment_feed) {

    override fun onStart() {
        super.onStart()

        post_fetch_in_progress.visibility = View.VISIBLE
        makeRequest()

    }

    private fun dummyData(size : Int): List<FeedItem>{
        val list = ArrayList<FeedItem>()
        val drawableImage = R.drawable.default_image
        for(i in 0 until size){
            val item = FeedItem(drawableImage,"author","date","description")
            list+=item
        }
        return list
        }

    private fun makeRequest() {
        val requestQueue = Volley.newRequestQueue(activity)
        requestQueue.add(FeedFetchMethod.PostFetch())
        requestQueue.addRequestFinishedListener<StringRequest>
        {

            val fetchData = fetchPostCall(FeedFetchMethod.responseFetch)
            Log.d("list",fetchData.toString())
            post_fetch_in_progress.visibility=View.GONE
            feed_recycler.adapter = FeedAdapter(fetchData)
            feed_recycler.layoutManager = LinearLayoutManager(activity,LinearLayoutManager.HORIZONTAL,false)
            feed_recycler.hasFixedSize()
        }
    }

    private fun fetchPostCall(response : JSONObject): List<FeedItem>{
        val list = ArrayList<FeedItem>()
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
                list+=dataPerPost
            }catch (e : Exception){
                Log.d("postFetchException",e.toString())
            }
        }
        return list
    }
}
