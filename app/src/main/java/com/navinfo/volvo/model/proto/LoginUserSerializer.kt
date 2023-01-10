package com.navinfo.volvo.model.proto

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import androidx.datastore.preferences.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

class LoginUserSerializer @Inject constructor(): Serializer<LoginUser> {
    override val defaultValue: LoginUser = LoginUser.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): LoginUser {
        try {
            return LoginUser.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: LoginUser, output: OutputStream) = t.writeTo(output)
}