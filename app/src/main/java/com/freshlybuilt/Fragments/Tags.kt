package com.freshlybuilt.Fragments


import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.freshlybuilt.Adapters.FeedAdapter
import com.freshlybuilt.Adapters.TagAdapter
import com.freshlybuilt.Modals.TagItem

import com.freshlybuilt.R
import kotlinx.android.synthetic.main.fragment_feed.*
import kotlinx.android.synthetic.main.fragment_tags.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


class Tags : Fragment(R.layout.fragment_tags) {
    private lateinit var requestQueue : RequestQueue
    private lateinit var adapter : TagAdapter

    private val API_CALL_FETCH_QUESTION : String = "https://freshlybuilt.com/newapi/wp/v2/question/?page="
    private val layoutManager = LinearLayoutManager(activity,LinearLayoutManager.VERTICAL,false)
    private val tagRecycler = tag_question_recycler

    private var scrollDistance : Int = 0
    private var controlsVisible : Boolean = true
    private val HIDE_THRESHOLD = 25
    private var isScrolling : Boolean = false
    private var nextSetLoading : Boolean = false
    private var page : Int = 1
    private val dataList = ArrayList<TagItem>()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        requestQueue = Volley.newRequestQueue(activity)
        pageRequest()



        tag_question_recycler.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val currentItems = layoutManager.childCount
                val totalItems = layoutManager.itemCount
                val scrollOutItems = layoutManager.findFirstVisibleItemPosition()
                val recyclerViewState = tag_question_recycler.layoutManager?.onSaveInstanceState()

                if ( isScrolling && (currentItems + scrollOutItems >= totalItems)){
                    if (!nextSetLoading) {
                        page+=1
                        pageRequest()
                        tag_question_recycler.layoutManager?.onRestoreInstanceState(recyclerViewState)
                        isScrolling =false
                    }

                    Log.d("Scroll","onScrolled Loading being called")
                }

                if (controlsVisible && scrollDistance >= HIDE_THRESHOLD) {
                    //hide();
                    searchVisibilityTransition("hide")
                    scrollDistance = 0
                    controlsVisible = false
                }
                else if (!controlsVisible && scrollDistance <= -HIDE_THRESHOLD) {
                    //show();
                    searchVisibilityTransition("show")
                    scrollDistance = 0
                    controlsVisible = true

                }

                if ((controlsVisible && dy > 0) || (!controlsVisible && dy < 0)) {
                    scrollDistance += dy
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                    isScrolling = true
                }
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE){
                    isScrolling = false
                }

            }


        })
    }

    private fun pageRequest(){
        loadingProgressAnim()
        val requestPostFetch : StringRequest = StringRequest(Request.Method.GET, API_CALL_FETCH_QUESTION+page,
            Response.Listener<String> { response ->
                try{
                    val responseJson = JSONArray(response)

                        for(i in 0 until(responseJson.length())){
                            val currentQuestion  = responseJson.getJSONObject(i)


                            val quesID : Int = currentQuestion.getInt("id")
                            val quesDATE : String = currentQuestion.getString("date")

                            val quesTitleObject = currentQuestion.getJSONObject("title")
                            val quesTitle = quesTitleObject.getString("rendered")

                            val quesContentObject = currentQuestion.getJSONObject("content")
                            val quesContent : String = quesContentObject.getString("rendered")

                            val quesAuthorID : Int = currentQuestion.getInt("author")
                            val quesCommentStatus : String = currentQuestion.getString("comment_status")
                            val quesPingStatus : String = currentQuestion.getString("ping_status")

                            dataList.add(TagItem(quesID,quesDATE,quesTitle,quesContent,quesAuthorID,quesCommentStatus,quesPingStatus))
                        }


                        if (this::adapter.isInitialized){
                            adapter.notifyDataSetChanged()
                            loadingProgressAnim()
                            Log.d("else","if called")
                        }else{
                            adapter = TagAdapter(dataList)
                            tag_question_recycler.adapter = adapter
                            tag_question_recycler.layoutManager = layoutManager
                            loadingProgressAnim()
                            //tag_question_recycler.layoutManager.startSmoothScroll(object : RecyclerView.SmoothScroller())
                        }
                }catch (e : JSONException) {
                    Log.d("fetch","exception")
                }
            }, Response.ErrorListener {
                Log.d("fetch","fetch error")
            })
        requestQueue.add(requestPostFetch)
    }

    private fun searchVisibilityTransition(state : String){
        if(state == "show"){

                TransitionManager.beginDelayedTransition(tag_search_card, AutoTransition())
                tag_search_view.visibility = View.GONE
                tag_search_card.visibility = View.GONE



        }
        if(state == "hide"){

                TransitionManager.beginDelayedTransition(tag_search_card, AutoTransition())
                tag_search_card.visibility = View.VISIBLE
                tag_search_view.visibility = View.VISIBLE



        }
    }

    private fun loadingProgressAnim(){
        if (tag_fetch_in_progress.visibility == View.GONE){
            TransitionManager.beginDelayedTransition(tag_loading_card, AutoTransition())
            tag_fetch_in_progress.visibility = View.VISIBLE
        }else{
            TransitionManager.beginDelayedTransition(tag_loading_card, AutoTransition())
            tag_fetch_in_progress.visibility = View.GONE
        }
    }
}
