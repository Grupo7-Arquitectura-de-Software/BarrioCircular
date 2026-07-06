import { obtenerClienteSupabase } from "./supabaseCliente";

const BUCKET_EVIDENCIAS = "evidencias";

export class ErrorSubidaEvidencia extends Error {
  constructor(mensaje, causa = null) {
    super(mensaje, causa ? { cause: causa } : undefined);
    this.name = "ErrorSubidaEvidencia";
  }
}

/**
 * Sube la foto de evidencia al bucket público `evidencias` de Supabase
 * Storage y devuelve su URL pública (HTTPS), requerida por el backend.
 */
export const subirEvidencia = async (archivo) => {
  if (!archivo) {
    throw new ErrorSubidaEvidencia("No se seleccionó ninguna imagen de evidencia.");
  }

  const supabase = obtenerClienteSupabase();
  const extension = archivo.name.includes(".") ? archivo.name.split(".").pop() : "jpg";
  const rutaArchivo = `${crypto.randomUUID()}.${extension}`;

  const { error } = await supabase.storage.from(BUCKET_EVIDENCIAS).upload(rutaArchivo, archivo, {
    contentType: archivo.type || "image/jpeg",
    upsert: false,
  });

  if (error) {
    throw new ErrorSubidaEvidencia("No se pudo subir la imagen de evidencia.", error);
  }

  const {
    data: { publicUrl },
  } = supabase.storage.from(BUCKET_EVIDENCIAS).getPublicUrl(rutaArchivo);

  return publicUrl;
};
