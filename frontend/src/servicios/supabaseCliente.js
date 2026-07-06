import { createClient } from "@supabase/supabase-js";

const URL_SUPABASE = import.meta.env.VITE_SUPABASE_URL;
const CLAVE_ANONIMA_SUPABASE = import.meta.env.VITE_SUPABASE_ANON_KEY;

let clienteSupabase = null;

export const obtenerClienteSupabase = () => {
  if (!URL_SUPABASE || !CLAVE_ANONIMA_SUPABASE) {
    throw new Error("Faltan VITE_SUPABASE_URL o VITE_SUPABASE_ANON_KEY en el .env del frontend.");
  }
  if (!clienteSupabase) {
    clienteSupabase = createClient(URL_SUPABASE, CLAVE_ANONIMA_SUPABASE);
  }
  return clienteSupabase;
};
