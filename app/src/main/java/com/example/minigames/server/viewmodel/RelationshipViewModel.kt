package com.example.minigames.server.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.minigames.server.model.CreateRequestBody
import com.example.minigames.server.model.Relationship
import com.example.minigames.server.network.RetrofitClient
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RelationshipViewModel : ViewModel() {

    private val _friendsLiveData = MutableLiveData<List<Int>>()
    val friendsLiveData: LiveData<List<Int>> get() = _friendsLiveData

    fun createRequest(requesterId: Int, recipientId: Int) {
        viewModelScope.launch {
            try {
                val body = CreateRequestBody(requesterId, recipientId)
                RetrofitClient.relationshipService.createRequest(body).enqueue(object : Callback<Relationship> {
                    override fun onResponse(call: Call<Relationship>, response: Response<Relationship>) {
                        if (response.isSuccessful) {
                            Log.d("createRequest", "Request created successfully: $body")
                        } else {
                            Log.e("createRequest", "Failed to create request: $body")
                        }
                    }

                    override fun onFailure(call: Call<Relationship>, t: Throwable) {
                        Log.e("createRequest", "Exception creating request: $requesterId to $recipientId", t)
                    }
                })
            } catch (e: Exception) {
                Log.e("createRequest", "Exception creating request: $requesterId to $recipientId", e)
            }
        }
    }

    fun fetchFriends(userId: Int) {
        val call = RetrofitClient.relationshipService.getFriends(userId)
        call.enqueue(object : Callback<List<Int>> {
            override fun onResponse(call: Call<List<Int>>, response: Response<List<Int>>) {
                if (response.isSuccessful) {
                    val friends = response.body()?: emptyList()
                    _friendsLiveData.postValue(friends)
                    Log.d("fetchFriends", "Friends retrieved successfully for user $userId: $friends")
                } else {
                    Log.e("fetchFriends", "Failed to retrieve friends for user $userId")
                }
            }

            override fun onFailure(call: Call<List<Int>>, t: Throwable) {
                Log.e("fetchFriends", "Exception retrieving friends for user $userId", t)
            }
        })
    }

    fun getSentRequests(userId: Int, callback: (List<Int>?) -> Unit) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.relationshipService.getSentRequests(userId).execute()
                if (response.isSuccessful) {
                    val sentRequests = response.body()
                    callback(sentRequests)
                    Log.d("getSentRequests", "Sent requests retrieved successfully for user $userId: $sentRequests")
                } else {
                    callback(null)
                    Log.e("getSentRequests", "Failed to retrieve sent requests for user $userId")
                }
            } catch (e: Exception) {
                callback(null)
                Log.e("getSentRequests", "Exception retrieving sent requests for user $userId", e)
            }
        }
    }

    fun getReceivedRequests(userId: Int, callback: (List<Int>?) -> Unit) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.relationshipService.getReceivedRequests(userId).execute()
                if (response.isSuccessful) {
                    val receivedRequests = response.body()
                    callback(receivedRequests)
                    Log.d("getReceivedRequests", "Received requests retrieved successfully for user $userId: $receivedRequests")
                } else {
                    callback(null)
                    Log.e("getReceivedRequests", "Failed to retrieve received requests for user $userId")
                }
            } catch (e: Exception) {
                callback(null)
                Log.e("getReceivedRequests", "Exception retrieving received requests for user $userId", e)
            }
        }
    }

    fun acceptRequest(requesterId: Int, recipientId: Int) {
        viewModelScope.launch {
            try {
                val body = CreateRequestBody(requesterId, recipientId)
                val response = RetrofitClient.relationshipService.acceptRequest(body).execute()
                if (response.isSuccessful) {
                    Log.d("acceptRequest", "Request accepted successfully: $body")
                } else {
                    Log.e("acceptRequest", "Failed to accept request: $body")
                }
            } catch (e: Exception) {
                Log.e("acceptRequest", "Exception accepting request: $requesterId to $recipientId", e)
            }
        }
    }

    fun rejectRequest(requesterId: Int, recipientId: Int) {
        viewModelScope.launch {
            try {
                val body = CreateRequestBody(requesterId, recipientId)
                val response = RetrofitClient.relationshipService.rejectRequest(body).execute()
                if (response.isSuccessful) {
                    Log.d("rejectRequest", "Request rejected successfully: $body")
                } else {
                    Log.e("rejectRequest", "Failed to reject request: $body")
                }
            } catch (e: Exception) {
                Log.e("rejectRequest", "Exception rejecting request: $requesterId to $recipientId", e)
            }
        }
    }

    fun removeFriend(requesterId: Int, recipientId: Int, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val body = CreateRequestBody(requesterId, recipientId)
                RetrofitClient.relationshipService.removeFriend(body).enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            callback(true)
                            Log.d("removeFriend", "Friend removed successfully: $body")
                        } else {
                            callback(false)
                            Log.e("removeFriend", "Failed to remove friend: $body")
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        callback(false)
                        Log.e("removeFriend", "Exception removing friend: $requesterId to $recipientId", t)
                    }
                })
            } catch (e: Exception) {
                callback(false)
                Log.e("removeFriend", "Exception removing friend: $requesterId to $recipientId", e)
            }
        }
    }

    fun areFriends(id1: Int, id2: Int, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.relationshipService.areFriends(id1, id2).execute()
                if (response.isSuccessful) {
                    val areFriends = response.body() ?: false
                    callback(areFriends)
                    Log.d("areFriends", "Friendship status retrieved successfully: $id1 and $id2 are friends: $areFriends")
                } else {
                    callback(false)
                    Log.e("areFriends", "Failed to retrieve friendship status: $id1 and $id2")
                }
            } catch (e: Exception) {
                callback(false)
                Log.e("areFriends", "Exception retrieving friendship status: $id1 and $id2", e)
            }
        }
    }

    fun hasSentRequest(id1: Int, id2: Int, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.relationshipService.hasSentRequest(id1, id2).execute()
                if (response.isSuccessful) {
                    val hasSentRequest = response.body() ?: false
                    callback(hasSentRequest)
                    Log.d("hasSentRequest", "Sent request status retrieved successfully: $id1 to $id2: $hasSentRequest")
                } else {
                    callback(false)
                    Log.e("hasSentRequest", "Failed to retrieve sent request status: $id1 to $id2")
                }
            } catch (e: Exception) {
                callback(false)
                Log.e("hasSentRequest", "Exception retrieving sent request status: $id1 to $id2", e)
            }
        }
    }
}