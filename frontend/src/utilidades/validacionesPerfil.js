// Reglas espejo de los value objects del backend (InformacionContacto y
// DocumentoIdentificacion). Si cambian allá, deben cambiar aquí.
const FORMATO_TELEFONO = /^(?:09\d{8}|0[2-7]\d{7}|\+593(?:9\d{8}|[2-7]\d{7}))$/;
const FORMATO_DOCUMENTO = /^(?:\d{10}|\d{13})$/;

export const normalizarTelefono = (telefono) => (telefono || "").replace(/[\s()-]/g, "");

export const normalizarDocumento = (documento) => (documento || "").replace(/[\s-]/g, "");

export const esTelefonoValido = (telefono) => FORMATO_TELEFONO.test(normalizarTelefono(telefono));

export const esDocumentoValido = (documento) =>
  FORMATO_DOCUMENTO.test(normalizarDocumento(documento));
