package com.example.minigames.server.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.minigames.server.model.CreateRequestBody
import com.example.minigames.server.model.Relationship
import com.example.minigames.server.network.RetrofitClient
import kotlinx.coroutines.launch

class RelationshipViewModel : ViewModel() {

    fun createRequest(requesterId: Int, recipientId: Int) {
        viewModelScope.launch {
            try {
                val body = CreateRequestBody(requesterId, recipientId)
                val response = RetrofitClient.relationshipService.createRequest(body).execute()
                if (response.isSuccessful) {
                    Log.d("createRequest", "Request created successfully: $body")
                } else {
                    Log.e("createRequest", "Failed to create request: $body")
                }
            } catch (e: Exception) {
                Log.e("createRequest", "Exception creating request: $requesterId to $recipientId", e)
            }
        }
    }

    fun getFriends(userId: Int, callback: (List<Int>?) -> Unit) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.relationshipService.getFriends(userId).execute()
                if (response.isSuccessful) {
                    val friends = response.body()
                    callback(friends)
                    Log.d("getFriends", "Friends retrieved successfully for user $userId: $friends")
                } else {
                    callback(null)
                    Log.e("getFriends", "Failed to retrieve friends for user $userId")
                }
            } catch (e: Exception) {
                callback(null)
                Log.e("getFriends", "Exception retrieving friends for user $userId", e)
            }
        }
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

    fun removeFriend(requesterId: Int, recipientId: Int) {
        viewModelScope.launch {
            try {
                val body = CreateRequestBody(requesterId, recipientId)
                val response = RetrofitClient.relationshipService.removeFriend(body).execute()
                if (response.isSuccessful) {
                    Log.d("removeFriend", "Friend removed successfully: $body")
                } else {
                    Log.e("removeFriend", "Failed to remove friend: $body")
                }
            } catch (e: Exception) {
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