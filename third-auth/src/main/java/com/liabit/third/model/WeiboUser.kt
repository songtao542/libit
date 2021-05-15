package com.liabit.third.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class WeiboUser(
    var token: String? = null,
    var uid: String? = null,
    var id: Long = 0,// "id": 1404376560,
    @SerializedName("screen_name") var screenName: String? = null,// "screen_name": "zaku",
    var name: String? = null,// "name": "zaku",
    var province: String? = null, // "province": "11",
    var city: String? = null,// "city": "5",
    var location: String? = null,// "location": "北京 朝阳区",
    var description: String? = null,// "description": "人生五十年，乃如梦如幻；有生斯有死，壮士复何憾。",
    var url: String? = null,//"url": "http://blog.sina.com.cn/zaku",
    @SerializedName("profile_image_url") var profileImageUrl: String? = null,//"profile_image_url": "http://tp1.sinaimg.cn/1404376560/50/0/1",
    var domain: String? = null,// "domain": "zaku",
    var gender: String? = null,//"gender": "m",
    @SerializedName("followers_count") var followersCount: Long = 0,// "followers_count": 1204,
    @SerializedName("friends_count") var friendsCount: Long = 0,//"friends_count": 447,
    @SerializedName("statuses_count") var statusesCount: Long = 0, //  "statuses_count": 2908,
    @SerializedName("favourites_count") var favouritesCount: Long = 0,//  "favourites_count": 0,
    @SerializedName("created_at") var createdAt: String? = null,// "created_at": "Fri Aug 28 00:00:00 +0800 2009",
    var following: Boolean = false,//  "following": false,
    @SerializedName("allow_all_act_msg") var allowAllActMsg: Boolean = false,//  "allow_all_act_msg": false,
    @SerializedName("geo_enabled") var geoEnabled: Boolean = false,//   "geo_enabled": true,
    var verified: Boolean = false,// "verified": false,
    var status: WeiboStatus? = null,//  "status": {
    @SerializedName("allow_all_comment") var allowAllComment: Boolean = false,// "allow_all_comment": true,
    @SerializedName("avatar_large") var avatarLarge: String? = null,// "avatar_large": "http://tp1.sinaimg.cn/1404376560/180/0/1",
    @SerializedName("verified_reason") var verifiedReason: String? = null,//"verified_reason": "",
    @SerializedName("follow_me") var followMe: Boolean = false,//  "follow_me": false,
    @SerializedName("online_status") var onlineStatus: Long = 0,// "online_status": 0,
    @SerializedName("bi_followers_count") var biFollowersCount: Long = 0// "bi_followers_count": 215
) : Parcelable

@Parcelize
data class WeiboStatus(
    @SerializedName("created_at") var created_at: String? = null,// "created_at": "Tue May 24 18:04:53 +0800 2011",
    var id: Long = 0,// "id": 11142488790,
    var text: String? = null,//   "text": "我的相机到了。",
    var source: String? = null,//"source": "<a href="http://weibo.com" rel="nofollow">新浪微博</a>",
    var favorited: Boolean = false,//  "favorited": false,
    var truncated: Boolean = false,//    "truncated": false,
    @SerializedName("in_reply_to_status_id") var inReplyToStatusId: String? = null,//    "in_reply_to_status_id": "",
    @SerializedName("in_reply_to_user_id") var inReplyToUserId: String? = null,//    "in_reply_to_user_id": "",
    @SerializedName("in_reply_to_screen_name") var inReplyToScreenName: String? = null,//    "in_reply_to_screen_name": "",
    var geo: String? = null,//    "geo": null,
    var mid: String? = null,//      "mid": "5610221544300749636",
    // "annotations": [],
    @SerializedName("reposts_count") var repostsCount: Long = 0,//    "reposts_count": 5,
    @SerializedName("comments_count") var commentsCount: Long = 0  // "comments_count": 8
) : Parcelable