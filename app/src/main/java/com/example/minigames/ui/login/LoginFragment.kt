package com.example.minigames.ui.login

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import com.example.minigames.ProfileViewModel
import com.example.minigames.R
import com.example.minigames.databinding.FragmentLoginBinding
import com.example.minigames.server.viewmodel.UserViewModel
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import java.io.File
import java.io.IOException
import java.net.URI


class LoginFragment : BaseFragment<FragmentLoginBinding>() {

    companion object {
        fun newInstance() = LoginFragment()
        private const val TAG = "LoginFragment"
    }

    private val viewModel: ProfileViewModel by activityViewModels()

    // 카카오 로그인
    val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        if (error != null) {
            Log.e(TAG, "카카오계정으로 로그인 실패", error)
        } else if (token != null) {
            Log.i(TAG, "카카오계정으로 로그인 성공 ${token.accessToken}")
            getUserInfoAndSave(token.accessToken)
        }
    }

    override fun initView() {
        val context: Context = requireContext()
        // 버튼 클릭했을 때 로그인
        with(binding) {
            kakaoLogin.setOnClickListener {
                // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
                if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
                    UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
                        if (error != null) {
                            Log.e(TAG, "카카오톡으로 로그인 실패", error)
                            if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                                return@loginWithKakaoTalk
                            }
                            UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
                        } else if (token != null) {
                            Log.i(TAG, "카카오톡으로 로그인 성공 ${token.accessToken}")
                            getUserInfoAndSave(token.accessToken)
                        }
                    }
                } else {
                    UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
                }
            }
        }
    }

    private fun getUserInfoAndSave(token: String) {
        UserApiClient.instance.me { user, error ->
            if (error != null) {
                Log.e(TAG, "사용자 정보 요청 실패", error)
            } else if (user != null) {
                Log.i(TAG, "사용자 정보 요청 성공: ${user.kakaoAccount?.profile?.nickname}")
                val userId = user.id
                val userNickname = user.kakaoAccount?.profile?.nickname
                val profileImageUrl = user.kakaoAccount?.profile?.profileImageUrl
                viewModel.saveLoginInfo(token, userId, userNickname, profileImageUrl)
                GoMain(userId, userNickname, profileImageUrl)
                Log.d("call gomain", "userId is $userId")
            }
        }
    }

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentLoginBinding {
        return FragmentLoginBinding.inflate(inflater, container, false)
    }
    private val userViewModel: UserViewModel by activityViewModels()

    /*private fun uriToFile(uri: Uri, context: Context): File? {
        try {
            val file = File(context.cacheDir, uri.lastPathSegment ?: "temp_file")
            val inputStream = context.contentResolver.openInputStream(uri)
            file.outputStream().use { outputStream ->
                inputStream?.copyTo(outputStream)
            }
            return file
        } catch (e: IOException) {
            Log.e(TAG, "Failed to convert Uri to File", e)
            return null
        }
    }*/

    private fun GoMain(userId: Long?, userNickname: String?,imageUrl: String?){
        // 로그인 -> 메인
        val id=userId?.toInt() ?: 0
        userViewModel.isThereId(id) { exists ->
            if (exists) {
                // ID가 존재하는 경우
                Log.d("checkUserId", "User with id $userId exists")
                // 여기서 UI 업데이트 등을 수행할 수 있음
                userViewModel.userScoreUp(id, 3);
            } else {
                // ID가 존재하지 않는 경우
                Log.d("checkUserId", "User with id $userId does not exist")
                //userViewModel.createUser(userId?.toInt() ?: 0, userNickname ?: "")
                // 여기서 UI 업데이트 등을 수행할 수 있음
                try{
                    userViewModel.uploadProfileImage(userId?.toInt() ?: 0, imageUrl)
                }
                catch (e: Exception){
                    Log.d("uploadProfileImage","was tried")
                }
            }
            Navigation.findNavController(binding.root).navigate(R.id.action_login_to_home)
        }
    }
}