package com.example.movil2.data.remote

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

object SoapRequestBuilder {

    private val MEDIA_TYPE = "text/xml; charset=utf-8".toMediaType()

    fun buildLoginBody(matricula: String, contrasenia: String, tipoUsuario: String): RequestBody {
        val xml = """
            <?xml version="1.0" encoding="utf-8"?>
            <soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                           xmlns:xsd="http://www.w3.org/2001/XMLSchema"
                           xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
              <soap:Body>
                <accesoLogin xmlns="http://tempuri.org/">
                  <strMatricula>$matricula</strMatricula>
                  <strContrasenia>$contrasenia</strContrasenia>
                  <tipoUsuario>$tipoUsuario</tipoUsuario>
                </accesoLogin>
              </soap:Body>
            </soap:Envelope>
        """.trimIndent()
        return xml.toRequestBody(MEDIA_TYPE)
    }

    fun buildProfileBody(): RequestBody {
        val xml = """
            <?xml version="1.0" encoding="utf-8"?>
            <soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                           xmlns:xsd="http://www.w3.org/2001/XMLSchema"
                           xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
              <soap:Body>
                <getAlumnoAcademicoWithLineamiento xmlns="http://tempuri.org/" />
              </soap:Body>
            </soap:Envelope>
        """.trimIndent()
        return xml.toRequestBody(MEDIA_TYPE)
    }

    fun buildCargaAcademicaBody(): RequestBody {
        val xml = """
            <?xml version="1.0" encoding="utf-8"?>
            <soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                           xmlns:xsd="http://www.w3.org/2001/XMLSchema"
                           xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
              <soap:Body>
                <getCargaAcademicaByAlumno xmlns="http://tempuri.org/" />
              </soap:Body>
            </soap:Envelope>
        """.trimIndent()
        return xml.toRequestBody(MEDIA_TYPE)
    }

    fun buildKardexBody(aluLineamiento: Int = 0): RequestBody {
        val xml = """
            <?xml version="1.0" encoding="utf-8"?>
            <soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                           xmlns:xsd="http://www.w3.org/2001/XMLSchema"
                           xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
              <soap:Body>
                <getAllKardexConPromedioByAlumno xmlns="http://tempuri.org/">
                  <aluLineamiento>$aluLineamiento</aluLineamiento>
                </getAllKardexConPromedioByAlumno>
              </soap:Body>
            </soap:Envelope>
        """.trimIndent()
        return xml.toRequestBody(MEDIA_TYPE)
    }

    fun buildCalifUnidadesBody(): RequestBody {
        val xml = """
            <?xml version="1.0" encoding="utf-8"?>
            <soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                           xmlns:xsd="http://www.w3.org/2001/XMLSchema"
                           xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
              <soap:Body>
                <getCalifUnidadesByAlumno xmlns="http://tempuri.org/" />
              </soap:Body>
            </soap:Envelope>
        """.trimIndent()
        return xml.toRequestBody(MEDIA_TYPE)
    }

    fun buildCalifFinalBody(bytModEducativo: Int = 0): RequestBody {
        val xml = """
            <?xml version="1.0" encoding="utf-8"?>
            <soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                           xmlns:xsd="http://www.w3.org/2001/XMLSchema"
                           xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
              <soap:Body>
                <getAllCalifFinalByAlumnos xmlns="http://tempuri.org/">
                  <bytModEducativo>$bytModEducativo</bytModEducativo>
                </getAllCalifFinalByAlumnos>
              </soap:Body>
            </soap:Envelope>
        """.trimIndent()
        return xml.toRequestBody(MEDIA_TYPE)
    }
}