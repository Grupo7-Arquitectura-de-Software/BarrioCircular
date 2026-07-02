const URL_BASE_API = (import.meta.env.VITE_API_BASE_URL || "http://localhost:8080/api")
    .replace(/\/$/, "");

export class ErrorApi extends Error {
    constructor(mensaje, estadoHttp, datos = null, causa = null) {
        super(mensaje, causa ? { cause: causa } : undefined);
        this.name = "ErrorApi";
        this.estadoHttp = estadoHttp;
        this.datos = datos;
    }
}

const leerCuerpoRespuesta = async (respuesta) => {
    if (respuesta.status === 204) return null;

    const tipoContenido = respuesta.headers.get("content-type") || "";
    if (tipoContenido.includes("application/json") || tipoContenido.includes("application/problem+json")) {
        return respuesta.json();
    }

    const texto = await respuesta.text();
    return texto || null;
};

const obtenerMensajeError = (respuesta, datos) => {
    if (datos && typeof datos === "object") {
        return datos.detail || datos.message || datos.title || `Error HTTP ${respuesta.status}`;
    }
    return datos || `Error HTTP ${respuesta.status}`;
};

export const solicitarApi = async (
    ruta,
    { metodo = "GET", token, cuerpo, encabezados = {} } = {},
) => {
    const encabezadosSolicitud = {
        Accept: "application/json",
        ...encabezados,
    };

    if (cuerpo !== undefined) encabezadosSolicitud["Content-Type"] = "application/json";
    if (token) encabezadosSolicitud.Authorization = `Bearer ${token}`;

    let respuesta;
    try {
        respuesta = await fetch(`${URL_BASE_API}${ruta}`, {
            method: metodo,
            headers: encabezadosSolicitud,
            body: cuerpo === undefined ? undefined : JSON.stringify(cuerpo),
        });
    } catch (causa) {
        throw new ErrorApi("No fue posible conectar con el backend.", 0, null, causa);
    }

    const datos = await leerCuerpoRespuesta(respuesta);
    if (!respuesta.ok) {
        throw new ErrorApi(obtenerMensajeError(respuesta, datos), respuesta.status, datos);
    }

    return datos;
};

export const esErrorApiConEstado = (error, estadoHttp) => (
    error instanceof ErrorApi && error.estadoHttp === estadoHttp
);
