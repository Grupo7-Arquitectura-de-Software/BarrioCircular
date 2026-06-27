import { useEffect, useState } from "react";
import { useAuth } from "@clerk/clerk-react";
import { useNavigate, useLocation } from "react-router-dom";
import { Box, Spinner, VStack, Text } from "@chakra-ui/react";

const ValidadorSesion = ({ children }) => {
    const { isLoaded, isSignedIn, getToken } = useAuth();
    const navigate = useNavigate();
    const location = useLocation();
    const [isVerifying, setIsVerifying] = useState(false);

    useEffect(() => {
        if (!isLoaded) return;

        if (!isSignedIn) {
            if (!location.pathname.startsWith("/auth")) {
                navigate("/auth", { replace: true });
            }
            return;
        }

        const verificarSesionBackend = async () => {
            setIsVerifying(true);
            try {
                const token = await getToken();
                const response = await fetch("http://localhost:8080/api/acceso/sesion", {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                    },
                    body: JSON.stringify({ tokenClerk: token }),
                });

                if (response.ok) {
                    const data = await response.json();
                    
                    // Si el backend dice que es nueva o falta completar el perfil
                    if (data.esNueva || data.estado === "PENDIENTE_PERFIL") {
                        if (location.pathname !== "/completar-perfil") {
                            navigate("/completar-perfil", { replace: true });
                        }
                    } else if (location.pathname === "/auth" || location.pathname === "/seleccionar-rol" || location.pathname === "/") {
                        // Aquí idealmente sabríamos el rol del usuario para redirigirlo. 
                        // Temporalmente podemos redirigirlo a un dashboard central o hacer un fetch a /api/perfiles/me
                        // Como PaginadeSeleccionRol.jsx redirigía a roles, por ahora redirigimos según lo que sepamos
                        // Si el rol no está en la sesión, habría que preguntar al backend.
                        // Para simplificar, mandaremos a /seleccionar-rol pero sabiendo que ya está autenticado, 
                        // Ojo, seleccion-rol no debe pedir auth de nuevo.
                        // Modificaremos PaginadeSeleccionRol para que no redirija a /auth/:rol, sino que envíe al dashboard del rol.
                        navigate("/seleccionar-rol", { replace: true });
                    }
                } else {
                    console.error("Error al verificar sesión en backend", response.status);
                }
            } catch (error) {
                console.error("Error de red al verificar sesión", error);
            } finally {
                setIsVerifying(false);
            }
        };

        // Evitar bucles: Solo verificar si acabamos de iniciar sesión o estamos en rutas base
        if (location.pathname.startsWith("/auth") || location.pathname === "/") {
            verificarSesionBackend();
        } else {
             // Si el usuario recarga la página, validamos también
             verificarSesionBackend();
        }
    }, [isLoaded, isSignedIn, getToken, navigate, location.pathname]);

    if (!isLoaded || isVerifying) {
        return (
            <VStack h="100vh" justify="center">
                <Spinner size="xl" />
                <Text>Validando sesión...</Text>
            </VStack>
        );
    }

    return children;
};

export default ValidadorSesion;
