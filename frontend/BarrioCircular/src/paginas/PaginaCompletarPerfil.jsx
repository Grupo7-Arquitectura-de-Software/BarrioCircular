import { useState } from "react";
import { useAuth } from "@clerk/clerk-react";
import { Box, Button, Input, NativeSelect, Text, VStack } from "@chakra-ui/react";
import { useNavigate } from "react-router-dom";

import LogotipoApp from "../componentes/atomos/LogotipoApp";
import DiseniodeAutenticacion from "../componentes/plantillas/DiseniodeAutenticacion.jsx";
import { toaster } from "@/components/ui/toaster";
import { esErrorApiConEstado } from "@/servicios/clienteApi";
import { completarPerfil } from "@/servicios/perfilService";
import { obtenerRutaPrincipalPorRol } from "@/utilidades/rutasPerfil";

const ROLES = [
    { value: "CIUDADANO", label: "Ciudadano" },
    { value: "RECICLADOR", label: "Reciclador" },
    { value: "CENTRO_RECOLECCION", label: "Centro de Recolección" },
];

const COORDENADAS_QUITO = {
    latitud: -0.1807,
    longitud: -78.4678,
};

const obtenerMensajeError = (error) => {
    if (esErrorApiConEstado(error, 403)) {
        return "La sesión no está autorizada para completar este perfil.";
    }
    if (esErrorApiConEstado(error, 404)) {
        return "No se encontró la cuenta asociada. Vuelve a iniciar sesión.";
    }
    if (esErrorApiConEstado(error, 409)) {
        return "Ya existe un perfil o documento registrado con estos datos.";
    }
    return "No se pudo completar el perfil. Intenta nuevamente.";
};

const PaginaCompletarPerfil = () => {
    const navigate = useNavigate();
    const { getToken } = useAuth();
    const [datosFormulario, setDatosFormulario] = useState({
        rol: "",
        documentoIdentificacion: "",
        nombreCompleto: "",
        nombreComercial: "",
        correoElectronico: "",
        telefono: "",
        ...COORDENADAS_QUITO,
    });
    const [estaEnviando, setEstaEnviando] = useState(false);

    const actualizarCampo = (evento) => {
        const { name, value } = evento.target;
        setDatosFormulario((datosActuales) => ({
            ...datosActuales,
            [name]: value,
        }));
    };

    const enviarFormulario = async (evento) => {
        evento.preventDefault();
        setEstaEnviando(true);

        try {
            const token = await getToken();
            if (!token) throw new Error("No se obtuvo un token de sesión de Clerk.");

            const datosPerfil = {
                ...datosFormulario,
                latitud: Number(datosFormulario.latitud),
                longitud: Number(datosFormulario.longitud),
            };
            const perfilCreado = await completarPerfil(token, datosPerfil);
            const rutaPrincipal = obtenerRutaPrincipalPorRol(perfilCreado.rol);

            if (!rutaPrincipal) {
                throw new Error(`El rol ${perfilCreado.rol} no tiene una ruta configurada.`);
            }

            toaster.create({
                title: "Perfil completado",
                description: "Tu perfil ha sido configurado exitosamente.",
                type: "success",
                duration: 3000,
            });

            navigate(rutaPrincipal, { replace: true });
        } catch (error) {
            toaster.create({
                title: "No se pudo completar el perfil",
                description: obtenerMensajeError(error),
                type: "error",
                duration: 4000,
            });
        } finally {
            setEstaEnviando(false);
        }
    };

    return (
        <DiseniodeAutenticacion>
            <VStack gap={6} align="stretch" w="100%">
                <Box textAlign="center" mb={4}>
                    <LogotipoApp tamanio="md" />
                    <Text fontSize="xl" fontWeight="bold" mt={4}>Completa tu perfil</Text>
                    <Text fontSize="sm" color="gray.600">
                        Necesitamos algunos datos para finalizar tu registro.
                    </Text>
                </Box>

                <form onSubmit={enviarFormulario} style={{ width: "100%" }}>
                    <VStack gap={4}>
                        <NativeSelect.Root>
                            <NativeSelect.Field
                                name="rol"
                                value={datosFormulario.rol}
                                onChange={actualizarCampo}
                                required
                            >
                                <option value="">Selecciona tu rol</option>
                                {ROLES.map((rol) => (
                                    <option key={rol.value} value={rol.value}>{rol.label}</option>
                                ))}
                            </NativeSelect.Field>
                        </NativeSelect.Root>

                        <Input
                            name="documentoIdentificacion"
                            placeholder="Documento de identidad / RUC"
                            value={datosFormulario.documentoIdentificacion}
                            onChange={actualizarCampo}
                            required
                        />

                        <Input
                            name="nombreCompleto"
                            placeholder="Nombre completo"
                            value={datosFormulario.nombreCompleto}
                            onChange={actualizarCampo}
                            required={datosFormulario.rol !== "CENTRO_RECOLECCION"}
                        />

                        {datosFormulario.rol === "CENTRO_RECOLECCION" && (
                            <Input
                                name="nombreComercial"
                                placeholder="Nombre comercial"
                                value={datosFormulario.nombreComercial}
                                onChange={actualizarCampo}
                                required
                            />
                        )}

                        <Input
                            name="correoElectronico"
                            type="email"
                            placeholder="Correo electrónico"
                            value={datosFormulario.correoElectronico}
                            onChange={actualizarCampo}
                            required
                        />

                        <Input
                            name="telefono"
                            type="tel"
                            placeholder="Teléfono de contacto"
                            value={datosFormulario.telefono}
                            onChange={actualizarCampo}
                            required
                        />

                        <Input
                            name="latitud"
                            type="number"
                            step="any"
                            placeholder="Latitud"
                            value={datosFormulario.latitud}
                            onChange={actualizarCampo}
                            required
                        />

                        <Input
                            name="longitud"
                            type="number"
                            step="any"
                            placeholder="Longitud"
                            value={datosFormulario.longitud}
                            onChange={actualizarCampo}
                            required
                        />

                        <Button
                            type="submit"
                            colorPalette="blue"
                            width="full"
                            loading={estaEnviando}
                            loadingText="Guardando perfil"
                        >
                            Completar registro
                        </Button>
                    </VStack>
                </form>
            </VStack>
        </DiseniodeAutenticacion>
    );
};

export default PaginaCompletarPerfil;
