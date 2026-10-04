package com.example.data.local.identity

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface IdentityDao {

    // User Profile
    @Query("SELECT * FROM cached_user_profiles WHERE uid = :uid LIMIT 1")
    fun getUserProfile(uid: String): Flow<UserProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfile(profile: UserProfileEntity)

    // Digital ID Card
    @Query("SELECT * FROM cached_digital_id_cards WHERE uid = :uid LIMIT 1")
    fun getDigitalIdCard(uid: String): Flow<DigitalIdCardEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDigitalIdCard(idCard: DigitalIdCardEntity)

    // User Devices
    @Query("SELECT * FROM cached_user_devices ORDER BY lastLoginTime DESC")
    fun getUserDevices(): Flow<List<UserDeviceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserDevices(devices: List<UserDeviceEntity>)

    @Query("DELETE FROM cached_user_devices WHERE deviceId = :deviceId")
    suspend fun deleteDevice(deviceId: String)

    @Query("DELETE FROM cached_user_devices WHERE isCurrentDevice = 0")
    suspend fun deleteAllOtherDevices()

    // Login History
    @Query("SELECT * FROM user_login_history ORDER BY loginTime DESC")
    fun getLoginHistory(): Flow<List<LoginHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLoginHistory(items: List<LoginHistoryEntity>)

    // User Settings
    @Query("SELECT * FROM cached_user_settings WHERE uid = :uid LIMIT 1")
    fun getUserSettings(uid: String): Flow<UserSettingsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserSettings(settings: UserSettingsEntity)
}
