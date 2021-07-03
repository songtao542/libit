#! /bin/bash

privileged_permissions[0]="android.permission.WRITE_OBB"
privileged_permissions[1]="android.permission.ACCESS_IMS_CALL_SERVICE"
privileged_permissions[2]="android.permission.EXEMPT_FROM_AUDIO_RECORD_RESTRICTIONS"
privileged_permissions[3]="android.permission.ACCESS_UCE_PRESENCE_SERVICE"
privileged_permissions[4]="android.permission.ACCESS_UCE_OPTIONS_SERVICE"
privileged_permissions[5]="android.permission.SEND_RESPOND_VIA_MESSAGE"
privileged_permissions[6]="android.permission.SEND_SMS_NO_CONFIRMATION"
privileged_permissions[7]="android.permission.CARRIER_FILTER_SMS"
privileged_permissions[8]="android.permission.RECEIVE_EMERGENCY_BROADCAST"
privileged_permissions[9]="android.permission.RECEIVE_BLUETOOTH_MAP"
privileged_permissions[10]="android.permission.BIND_DIRECTORY_SEARCH"
privileged_permissions[11]="android.permission.MODIFY_CELL_BROADCASTS"
privileged_permissions[12]="com.android.voicemail.permission.WRITE_VOICEMAIL"
privileged_permissions[13]="com.android.voicemail.permission.READ_VOICEMAIL"
privileged_permissions[14]="android.permission.INSTALL_LOCATION_PROVIDER"
privileged_permissions[15]="android.permission.HDMI_CEC"
privileged_permissions[16]="android.permission.LOCATION_HARDWARE"
privileged_permissions[17]="android.permission.ACCESS_CONTEXT_HUB"
privileged_permissions[18]="android.permission.READ_WIFI_CREDENTIAL"
privileged_permissions[19]="android.permission.TETHER_PRIVILEGED"
privileged_permissions[20]="android.permission.RECEIVE_WIFI_CREDENTIAL_CHANGE"
privileged_permissions[21]="android.permission.OVERRIDE_WIFI_CONFIG"
privileged_permissions[22]="android.permission.SCORE_NETWORKS"
privileged_permissions[23]="android.permission.NETWORK_CARRIER_PROVISIONING"
privileged_permissions[24]="android.permission.ACCESS_LOWPAN_STATE"
privileged_permissions[25]="android.permission.CHANGE_LOWPAN_STATE"
privileged_permissions[26]="android.permission.READ_LOWPAN_CREDENTIAL"
privileged_permissions[27]="android.permission.MANAGE_LOWPAN_INTERFACES"
privileged_permissions[28]="android.permission.WIFI_SET_DEVICE_MOBILITY_STATE"
privileged_permissions[29]="android.permission.WIFI_UPDATE_USABILITY_STATS_SCORE"
privileged_permissions[30]="android.permission.BLUETOOTH_PRIVILEGED"
privileged_permissions[31]="android.permission.SECURE_ELEMENT_PRIVILEGED_OPERATION"
privileged_permissions[32]="android.permission.CONNECTIVITY_INTERNAL"
privileged_permissions[33]="android.permission.CONNECTIVITY_USE_RESTRICTED_NETWORKS"
privileged_permissions[34]="android.permission.NETWORK_SIGNAL_STRENGTH_WAKEUP"
privileged_permissions[35]="android.permission.PACKET_KEEPALIVE_OFFLOAD"
privileged_permissions[36]="android.permission.RECEIVE_DATA_ACTIVITY_CHANGE"
privileged_permissions[37]="android.permission.LOOP_RADIO"
privileged_permissions[38]="android.permission.NFC_HANDOVER_STATUS"
privileged_permissions[39]="android.permission.ACCESS_VIBRATOR_STATE"
privileged_permissions[40]="android.permission.MANAGE_FACTORY_RESET_PROTECTION"
privileged_permissions[41]="android.permission.MANAGE_USB"
privileged_permissions[42]="android.permission.MANAGE_DEBUGGING"
privileged_permissions[43]="android.permission.ACCESS_MTP"
privileged_permissions[44]="android.permission.INSTALL_DYNAMIC_SYSTEM"
privileged_permissions[45]="android.permission.ACCESS_BROADCAST_RADIO"
privileged_permissions[46]="android.permission.ACCESS_FM_RADIO"
privileged_permissions[47]="android.permission.TV_INPUT_HARDWARE"
privileged_permissions[48]="android.permission.CAPTURE_TV_INPUT"
privileged_permissions[49]="android.permission.DVB_DEVICE"
privileged_permissions[50]="android.permission.MANAGE_CARRIER_OEM_UNLOCK_STATE"
privileged_permissions[51]="android.permission.MANAGE_USER_OEM_UNLOCK_STATE"
privileged_permissions[52]="android.permission.READ_OEM_UNLOCK_STATE"
privileged_permissions[53]="android.permission.NOTIFY_PENDING_SYSTEM_UPDATE"
privileged_permissions[54]="android.permission.CAMERA_DISABLE_TRANSMIT_LED"
privileged_permissions[55]="android.permission.CAMERA_SEND_SYSTEM_EVENTS"
privileged_permissions[56]="android.permission.MODIFY_PHONE_STATE"
privileged_permissions[57]="android.permission.READ_PRECISE_PHONE_STATE"
privileged_permissions[58]="android.permission.READ_PRIVILEGED_PHONE_STATE"
privileged_permissions[59]="android.permission.REGISTER_SIM_SUBSCRIPTION"
privileged_permissions[60]="android.permission.REGISTER_CALL_PROVIDER"
privileged_permissions[61]="android.permission.REGISTER_CONNECTION_MANAGER"
privileged_permissions[62]="android.permission.BIND_INCALL_SERVICE"
privileged_permissions[63]="android.permission.NETWORK_SCAN"
privileged_permissions[64]="android.permission.BIND_VISUAL_VOICEMAIL_SERVICE"
privileged_permissions[65]="android.permission.BIND_SCREENING_SERVICE"
privileged_permissions[66]="android.permission.BIND_CALL_REDIRECTION_SERVICE"
privileged_permissions[67]="android.permission.BIND_CONNECTION_SERVICE"
privileged_permissions[68]="android.permission.BIND_TELECOM_CONNECTION_SERVICE"
privileged_permissions[69]="android.permission.CONTROL_INCALL_EXPERIENCE"
privileged_permissions[70]="android.permission.RECEIVE_STK_COMMANDS"
privileged_permissions[71]="android.permission.SEND_EMBMS_INTENTS"
privileged_permissions[72]="android.permission.BIND_IMS_SERVICE"
privileged_permissions[73]="android.permission.WRITE_EMBEDDED_SUBSCRIPTIONS"
privileged_permissions[74]="android.permission.WRITE_MEDIA_STORAGE"
privileged_permissions[75]="android.permission.ALLOCATE_AGGRESSIVE"
privileged_permissions[76]="android.permission.USE_RESERVED_DISK"
privileged_permissions[77]="android.permission.REAL_GET_TASKS"
privileged_permissions[78]="android.permission.START_TASKS_FROM_RECENTS"
privileged_permissions[79]="android.permission.INTERACT_ACROSS_USERS"
privileged_permissions[80]="android.permission.MANAGE_USERS"
privileged_permissions[81]="android.permission.ACTIVITY_EMBEDDING"
privileged_permissions[82]="android.permission.START_ACTIVITIES_FROM_BACKGROUND"
privileged_permissions[83]="android.permission.GET_PROCESS_STATE_AND_OOM_SCORE"
privileged_permissions[84]="android.permission.SET_DISPLAY_OFFSET"
privileged_permissions[85]="android.permission.COMPANION_APPROVE_WIFI_CONNECTIONS"
privileged_permissions[86]="android.permission.READ_WALLPAPER_INTERNAL"
privileged_permissions[87]="android.permission.SET_TIME"
privileged_permissions[88]="android.permission.SET_TIME_ZONE"
privileged_permissions[89]="android.permission.CHANGE_CONFIGURATION"
privileged_permissions[90]="android.permission.WRITE_GSERVICES"
privileged_permissions[91]="android.permission.FORCE_STOP_PACKAGES"
privileged_permissions[92]="android.permission.RETRIEVE_WINDOW_CONTENT"
privileged_permissions[93]="android.permission.SET_ANIMATION_SCALE"
privileged_permissions[94]="android.permission.MOUNT_UNMOUNT_FILESYSTEMS"
privileged_permissions[95]="android.permission.MOUNT_FORMAT_FILESYSTEMS"
privileged_permissions[96]="android.permission.WRITE_APN_SETTINGS"
privileged_permissions[97]="android.permission.CLEAR_APP_CACHE"
privileged_permissions[98]="android.permission.ALLOW_ANY_CODEC_FOR_PLAYBACK"
privileged_permissions[99]="android.permission.MANAGE_CA_CERTIFICATES"
privileged_permissions[100]="android.permission.RECOVERY"
privileged_permissions[101]="android.permission.UPDATE_CONFIG"
privileged_permissions[102]="android.permission.QUERY_TIME_ZONE_RULES"
privileged_permissions[103]="android.permission.UPDATE_TIME_ZONE_RULES"
privileged_permissions[104]="android.permission.CHANGE_OVERLAY_PACKAGES"
privileged_permissions[105]="android.permission.WRITE_SECURE_SETTINGS"
privileged_permissions[106]="android.permission.DUMP"
privileged_permissions[107]="android.permission.READ_LOGS"
privileged_permissions[108]="android.permission.SET_DEBUG_APP"
privileged_permissions[109]="android.permission.SET_PROCESS_LIMIT"
privileged_permissions[110]="android.permission.SET_ALWAYS_FINISH"
privileged_permissions[111]="android.permission.SIGNAL_PERSISTENT_PROCESSES"
privileged_permissions[112]="android.permission.REQUEST_INCIDENT_REPORT_APPROVAL"
privileged_permissions[113]="android.permission.GET_ACCOUNTS_PRIVILEGED"
privileged_permissions[114]="android.permission.STATUS_BAR"
privileged_permissions[115]="android.permission.UPDATE_DEVICE_STATS"
privileged_permissions[116]="android.permission.GET_APP_OPS_STATS"
privileged_permissions[117]="android.permission.UPDATE_APP_OPS_STATS"
privileged_permissions[118]="android.permission.SHUTDOWN"
privileged_permissions[119]="android.permission.STOP_APP_SWITCHES"
privileged_permissions[120]="android.permission.BIND_WALLPAPER"
privileged_permissions[121]="android.permission.MANAGE_VOICE_KEYPHRASES"
privileged_permissions[122]="android.permission.KEYPHRASE_ENROLLMENT_APPLICATION"
privileged_permissions[123]="android.permission.BIND_TV_INPUT"
privileged_permissions[124]="android.permission.BIND_TV_REMOTE_SERVICE"
privileged_permissions[125]="android.permission.TV_VIRTUAL_REMOTE_CONTROLLER"
privileged_permissions[126]="android.permission.CHANGE_HDMI_CEC_ACTIVE_SOURCE"
privileged_permissions[127]="android.permission.MODIFY_PARENTAL_CONTROLS"
privileged_permissions[128]="android.permission.READ_CONTENT_RATING_SYSTEMS"
privileged_permissions[129]="android.permission.NOTIFY_TV_INPUTS"
privileged_permissions[130]="android.permission.TUNER_RESOURCE_ACCESS"
privileged_permissions[131]="android.permission.RESET_PASSWORD"
privileged_permissions[132]="android.permission.LOCK_DEVICE"
privileged_permissions[133]="android.permission.INSTALL_PACKAGES"
privileged_permissions[134]="android.permission.INSTALL_SELF_UPDATES"
privileged_permissions[135]="android.permission.INSTALL_PACKAGE_UPDATES"
privileged_permissions[136]="com.android.permission.INSTALL_EXISTING_PACKAGES"
privileged_permissions[137]="android.permission.DELETE_CACHE_FILES"
privileged_permissions[138]="android.permission.DELETE_PACKAGES"
privileged_permissions[139]="android.permission.MOVE_PACKAGE"
privileged_permissions[140]="android.permission.CHANGE_COMPONENT_ENABLED_STATE"
privileged_permissions[141]="android.permission.OBSERVE_GRANT_REVOKE_PERMISSIONS"
privileged_permissions[142]="android.permission.CONTROL_DEVICE_LIGHTS"
privileged_permissions[143]="android.permission.CONTROL_DISPLAY_SATURATION"
privileged_permissions[144]="android.permission.CONTROL_DISPLAY_COLOR_TRANSFORMS"
privileged_permissions[145]="android.permission.BRIGHTNESS_SLIDER_USAGE"
privileged_permissions[146]="android.permission.ACCESS_AMBIENT_LIGHT_STATS"
privileged_permissions[147]="android.permission.CONFIGURE_DISPLAY_BRIGHTNESS"
privileged_permissions[148]="android.permission.CONTROL_VPN"
privileged_permissions[149]="android.permission.CAPTURE_AUDIO_OUTPUT"
privileged_permissions[150]="android.permission.CAPTURE_MEDIA_OUTPUT"
privileged_permissions[151]="android.permission.CAPTURE_VOICE_COMMUNICATION_OUTPUT"
privileged_permissions[152]="android.permission.CAPTURE_AUDIO_HOTWORD"
privileged_permissions[153]="android.permission.MODIFY_AUDIO_ROUTING"
privileged_permissions[154]="android.permission.MODIFY_DEFAULT_AUDIO_EFFECTS"
privileged_permissions[155]="android.permission.REMOTE_DISPLAY_PROVIDER"
privileged_permissions[156]="android.permission.MEDIA_CONTENT_CONTROL"
privileged_permissions[157]="android.permission.SET_VOLUME_KEY_LONG_PRESS_LISTENER"
privileged_permissions[158]="android.permission.SET_MEDIA_KEY_LISTENER"
privileged_permissions[159]="android.permission.REBOOT"
privileged_permissions[160]="android.permission.POWER_SAVER"
privileged_permissions[161]="android.permission.USER_ACTIVITY"
privileged_permissions[162]="android.permission.BROADCAST_NETWORK_PRIVILEGED"
privileged_permissions[163]="android.permission.MASTER_CLEAR"
privileged_permissions[164]="android.permission.CALL_PRIVILEGED"
privileged_permissions[165]="android.permission.PERFORM_CDMA_PROVISIONING"
privileged_permissions[166]="android.permission.PERFORM_SIM_ACTIVATION"
privileged_permissions[167]="android.permission.CONTROL_LOCATION_UPDATES"
privileged_permissions[168]="android.permission.ACCESS_CHECKIN_PROPERTIES"
privileged_permissions[169]="android.permission.PACKAGE_USAGE_STATS"
privileged_permissions[170]="android.permission.LOADER_USAGE_STATS"
privileged_permissions[171]="android.permission.OBSERVE_APP_USAGE"
privileged_permissions[172]="android.permission.CHANGE_APP_IDLE_STATE"
privileged_permissions[173]="android.permission.CHANGE_DEVICE_IDLE_TEMP_WHITELIST"
privileged_permissions[174]="android.permission.BATTERY_STATS"
privileged_permissions[175]="android.permission.REGISTER_STATS_PULL_ATOM"
privileged_permissions[176]="android.permission.BACKUP"
privileged_permissions[177]="android.permission.RECOVER_KEYSTORE"
privileged_permissions[178]="android.permission.BIND_REMOTEVIEWS"
privileged_permissions[179]="android.permission.BIND_APPWIDGET"
privileged_permissions[180]="android.permission.BIND_KEYGUARD_APPWIDGET"
privileged_permissions[181]="android.permission.MODIFY_APPWIDGET_BIND_PERMISSIONS"
privileged_permissions[182]="android.permission.GLOBAL_SEARCH"
privileged_permissions[183]="android.permission.READ_SEARCH_INDEXABLES"
privileged_permissions[184]="android.permission.WRITE_SETTINGS_HOMEPAGE_DATA"
privileged_permissions[185]="android.permission.SET_WALLPAPER_COMPONENT"
privileged_permissions[186]="android.permission.READ_DREAM_STATE"
privileged_permissions[187]="android.permission.WRITE_DREAM_STATE"
privileged_permissions[188]="android.permission.ACCESS_CACHE_FILESYSTEM"
privileged_permissions[189]="android.permission.CRYPT_KEEPER"
privileged_permissions[190]="android.permission.READ_NETWORK_USAGE_HISTORY"
privileged_permissions[191]="android.permission.MODIFY_NETWORK_ACCOUNTING"
privileged_permissions[192]="android.permission.MANAGE_SUBSCRIPTION_PLANS"
privileged_permissions[193]="android.permission.PACKAGE_VERIFICATION_AGENT"
privileged_permissions[194]="android.permission.MANAGE_ROLLBACKS"
privileged_permissions[195]="android.permission.INTENT_FILTER_VERIFICATION_AGENT"
privileged_permissions[196]="android.permission.SERIAL_PORT"
privileged_permissions[197]="android.permission.UPDATE_LOCK"
privileged_permissions[198]="android.permission.REQUEST_NOTIFICATION_ASSISTANT_SERVICE"
privileged_permissions[199]="android.permission.ACCESS_NOTIFICATIONS"
privileged_permissions[200]="android.permission.MANAGE_FINGERPRINT"
privileged_permissions[201]="android.permission.CONTROL_KEYGUARD_SECURE_NOTIFICATIONS"
privileged_permissions[202]="android.permission.PROVIDE_TRUST_AGENT"
privileged_permissions[203]="android.permission.SHOW_KEYGUARD_MESSAGE"
privileged_permissions[204]="android.permission.LAUNCH_TRUST_AGENT_SETTINGS"
privileged_permissions[205]="android.permission.PROVIDE_RESOLVER_RANKER_SERVICE"
privileged_permissions[206]="android.permission.INVOKE_CARRIER_SETUP"
privileged_permissions[207]="android.permission.ACCESS_NETWORK_CONDITIONS"
privileged_permissions[208]="android.permission.ACCESS_DRM_CERTIFICATES"
privileged_permissions[209]="android.permission.REMOVE_DRM_CERTIFICATES"
privileged_permissions[210]="android.permission.BIND_CARRIER_MESSAGING_SERVICE"
privileged_permissions[211]="android.permission.BIND_CARRIER_SERVICES"
privileged_permissions[212]="android.permission.LOCAL_MAC_ADDRESS"
privileged_permissions[213]="android.permission.DISPATCH_NFC_MESSAGE"
privileged_permissions[214]="android.permission.MODIFY_DAY_NIGHT_MODE"
privileged_permissions[215]="android.permission.ENTER_CAR_MODE_PRIORITIZED"
privileged_permissions[216]="android.permission.HANDLE_CAR_MODE_CHANGES"
privileged_permissions[217]="android.permission.RECEIVE_MEDIA_RESOURCE_USAGE"
privileged_permissions[218]="android.permission.MANAGE_SOUND_TRIGGER"
privileged_permissions[219]="android.permission.DISPATCH_PROVISIONING_MESSAGE"
privileged_permissions[220]="android.permission.SUBSTITUTE_NOTIFICATION_APP_NAME"
privileged_permissions[221]="android.permission.NOTIFICATION_DURING_SETUP"
privileged_permissions[222]="android.permission.READ_RUNTIME_PROFILES"
privileged_permissions[223]="android.permission.MODIFY_QUIET_MODE"
privileged_permissions[224]="android.permission.CONTROL_REMOTE_APP_TRANSITION_ANIMATIONS"
privileged_permissions[225]="android.permission.WATCH_APPOPS"
privileged_permissions[226]="android.permission.MONITOR_DEFAULT_SMS_PACKAGE"
privileged_permissions[227]="android.permission.BIND_EXPLICIT_HEALTH_CHECK_SERVICE"
privileged_permissions[228]="android.permission.SEND_DEVICE_CUSTOMIZATION_READY"
privileged_permissions[229]="android.permission.SUBSTITUTE_SHARE_TARGET_APP_NAME_AND_ICON"
privileged_permissions[230]="android.permission.LOG_COMPAT_CHANGE"
privileged_permissions[231]="android.permission.READ_COMPAT_CHANGE_CONFIG"
privileged_permissions[232]="android.permission.OVERRIDE_COMPAT_CHANGE_CONFIG"
privileged_permissions[233]="android.permission.ACCESS_TV_TUNER"
privileged_permissions[234]="android.permission.ACCESS_TV_DESCRAMBLER"

to_check_xml_file=$1

if [[ -f "${to_check_xml_file}" ]]; then
  for element in "${privileged_permissions[@]}"; do
    grep -in "${element}" "${to_check_xml_file}"
  done
else
  echo "待检查的文件：${to_check_xml_file} 不存在"
fi
