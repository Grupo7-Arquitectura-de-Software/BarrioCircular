import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { Box, VStack, Text, Input, Select, Button } from "@chakra-ui/react";
import { useAuth } from "@clerk/clerk-react";
import { toaster } from "@/components/ui/toaster";
import DiseniodeAutenticacion from "../componentes/plantillas/DiseniodeAutenticacion.jsx";
import LogotipoApp from "../componentes/atomos/LogotipoApp";

const ROLES = [
    { value: "CIUDADANO", label: "Ciudadano" },
    { value: "RECOLECTOR", label: "Recolector" },
    { value: "CENTRO_ACOPIO", label: "Centro de Acopio" }
];

const PaginaCompletarPerfil = () => {
    const navigate = useNavigate();
    const { getToken } = useAuth();
    
    const [formData, setFormData] = useState({
        rol: "",
        documentoIdentificacion: "",
        nombreCompleto: "",
        nombreComercial: "",
        correoElectronico: "",
        telefono: "",
        latitud: 0,
        longitud: 0
    });
    
    const [isLoading, setIsLoading] = useState(false);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({ ...prev, [name]: value }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setIsLoading(true);

        try {
            const token = await getToken();
            const response = await fetch("http://localhost:8080/api/perfiles/completar", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${token}`
                },
                body: JSON.stringify(formData)
            });

            if (response.ok) {
                toaster.create({
                    title: "Perfil completado",
                    description: "Tu perfil ha sido configurado exitosamente.",
                    type: "success",
                    duration: 3000,
                });
                
                // Redirigir según el rol
                if (formData.rol === "CIUDADANO") navigate("/ciudadano/crear-publicacion");
                else if (formData.rol === "RECOLECTOR") navigate("/recolector/inicio");
                else if (formData.rol === "CENTRO_ACOPIO") navigate("/centro/buscar-materiales");
                else navigate("/");
            } else {
                toaster.create({
                    title: "Error",
                    description: "No se pudo completar el perfil. Intenta nuevamente.",
                    type: "error",
                    duration: 3000,
                });
            }
        } catch (error) {
            console.error("Error al completar perfil:", error);
            toaster.create({
                title: "Error de red",
                description: "Ocurrió un error al contactar al servidor.",
                type: "error",
                duration: 3000,
            });
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <DiseniodeAutenticacion>
            <VStack gap={6} align="stretch" w="100%">
                <Box align="center" mb={4}>
                    <LogotipoApp tamanio="md" />
                    <Text fontSize="xl" fontWeight="bold" mt={4}>Completa tu Perfil</Text>
                    <Text fontSize="sm" color="gray.600">Necesitamos algunos datos para finalizar tu registro.</Text>
                </Box>
                
                <form onSubmit={handleSubmit} style={{ width: '100%' }}>
                    <VStack gap={4}>
                        <Select 
                            name="rol" 
                            placeholder="Selecciona tu rol" 
                            value={formData.rol} 
                            onChange={handleChange}
                            required
                        >
                            {ROLES.map(r => (
                                <option key={r.value} value={r.value}>{r.label}</option>
                            ))}
                        </Select>
                        
                        <Input 
                            name="documentoIdentificacion" 
                            placeholder="Documento de Identidad / NIT" 
                            value={formData.documentoIdentificacion}
                            onChange={handleChange}
                            required
                        />
                        
                        <Input 
                            name="nombreCompleto" 
                            placeholder="Nombre Completo" 
                            value={formData.nombreCompleto}
                            onChange={handleChange}
                            required
                        />
                        
                        {formData.rol !== "CIUDADANO" && (
                            <Input 
                                name="nombreComercial" 
                                placeholder="Nombre Comercial (Opcional)" 
                                value={formData.nombreComercial}
                                onChange={handleChange}
                            />
                        )}
                        
                        <Input 
                            name="correoElectronico" 
                            type="email"
                            placeholder="Correo Electrónico" 
                            value={formData.correoElectronico}
                            onChange={handleChange}
                            required
                        />
                        
                        <Input 
                            name="telefono" 
                            type="tel"
                            placeholder="Teléfono de contacto" 
                            value={formData.telefono}
                            onChange={handleChange}
                            required
                        />
                        
                        <Button 
                            type="submit" 
                            colorScheme="blue" 
                            width="full" 
                            isLoading={isLoading}
                        >
                            Completar Registro
                        </Button>
                    </VStack>
                </form>
            </VStack>
        </DiseniodeAutenticacion>
    );
};

export default PaginaCompletarPerfil;
