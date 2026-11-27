package com.hygeonix.AcreditaTech.network;

import com.hygeonix.AcreditaTech.modelo.AvanceResponse;
import com.hygeonix.AcreditaTech.modelo.ConstanciaResponse;
import com.hygeonix.AcreditaTech.modelo.EditarPerfilRequest;
import com.hygeonix.AcreditaTech.modelo.GuiasResponse;
import com.hygeonix.AcreditaTech.modelo.LoginRequest;
import com.hygeonix.AcreditaTech.modelo.LoginResponse;
import com.hygeonix.AcreditaTech.modelo.MensajeResponse;
import com.hygeonix.AcreditaTech.modelo.PerfilResponse;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;
import retrofit2.http.Streaming;

/**
 * Rutas del servidor que usa la app (Backend/rutas en el repo AcreditaTech).
 * El token lo agrega AuthInterceptor; no hace falta pasarlo aquí.
 * Registro y tests se hacen solo en la web.
 */
public interface ApiService {

    // POST /api/acceso/iniciar-sesion → { mensaje, token, id_tipo_usuario }
    @POST("api/acceso/iniciar-sesion")
    Call<LoginResponse> iniciarSesion(@Body LoginRequest body);

    // GET /api/perfil → { perfil: {...} }
    @GET("api/perfil")
    Call<PerfilResponse> perfil();

    // PUT /api/perfil → { mensaje }
    @PUT("api/perfil")
    Call<MensajeResponse> editarPerfil(@Body EditarPerfilRequest body);

    // GET /api/avance → { porcentaje, estatus, tecnica, salud }
    @GET("api/avance")
    Call<AvanceResponse> avance();

    // GET /api/guias?tipo=1|2 → { guias: [...] } (el servidor filtra por el oficio del usuario)
    @GET("api/guias")
    Call<GuiasResponse> guias(@Query("tipo") Integer tipo);

    // GET /api/constancia → ¿tiene Pre-Constancia? ¿puede generarla?
    @GET("api/constancia")
    Call<ConstanciaResponse> constancia();

    // GET /api/constancia/pdf → archivo PDF (no JSON)
    @Streaming
    @GET("api/constancia/pdf")
    Call<ResponseBody> descargarConstancia();
}
