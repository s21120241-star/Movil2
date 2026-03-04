package com.example.movil2.data.remote

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface Sicenetservice {

    @Headers("Content-Type: text/xml; charset=utf-8", "SOAPAction: \"http://tempuri.org/accesoLogin\"")
    @POST("ws/wsalumnos.asmx")
    suspend fun login(@Body body: RequestBody): Response<ResponseBody>

    @Headers("Content-Type: text/xml; charset=utf-8", "SOAPAction: \"http://tempuri.org/getAlumnoAcademicoWithLineamiento\"")
    @POST("ws/wsalumnos.asmx")
    suspend fun getProfile(@Body body: RequestBody): Response<ResponseBody>

    @Headers("Content-Type: text/xml; charset=utf-8", "SOAPAction: \"http://tempuri.org/getCargaAcademicaByAlumno\"")
    @POST("ws/wsalumnos.asmx")
    suspend fun getCargaAcademica(@Body body: RequestBody): Response<ResponseBody>

    @Headers("Content-Type: text/xml; charset=utf-8", "SOAPAction: \"http://tempuri.org/getAllKardexConPromedioByAlumno\"")
    @POST("ws/wsalumnos.asmx")
    suspend fun getKardex(@Body body: RequestBody): Response<ResponseBody>

    @Headers("Content-Type: text/xml; charset=utf-8", "SOAPAction: \"http://tempuri.org/getCalifUnidadesByAlumno\"")
    @POST("ws/wsalumnos.asmx")
    suspend fun getCalifUnidades(@Body body: RequestBody): Response<ResponseBody>

    @Headers("Content-Type: text/xml; charset=utf-8", "SOAPAction: \"http://tempuri.org/getAllCalifFinalByAlumnos\"")
    @POST("ws/wsalumnos.asmx")
    suspend fun getCalifFinal(@Body body: RequestBody): Response<ResponseBody>
}