import { VStack, Text } from "@chakra-ui/react";
import TarjetaPublicacionRecomendada from "../moleculas/TarjetaPublicacionRecomendada";

const PUBLICACIONES_EJEMPLO = [
    {
        id: 1,
        tipoMaterial: "Plástico PET",
        iconoMaterial: "plastico",
        pesoEstimado: 15,
        precioReferencial: "$2.50/kg",
        distancia: "3.2 km",
        puntuacion: 9.2,
    },
    {
        id: 2,
        tipoMaterial: "Metal",
        iconoMaterial: "metal",
        pesoEstimado: 15,
        precioReferencial: "$2.50/kg",
        distancia: "3.2 km",
        puntuacion: 9.2,
    },
    {
        id: 3,
        tipoMaterial: "Papel",
        iconoMaterial: "papel",
        pesoEstimado: 15,
        precioReferencial: "$2.50/kg",
        distancia: "3.2 km",
        puntuacion: 9.2,
    },
];

/**
 * Organismo: Lista de publicaciones recomendadas por Match Inteligente
 * @param {Array} publicaciones - Lista de publicaciones
 * @param {function} alSeleccionar - Callback con id de la publicación seleccionada
 */
const ListaPublicacionesRecomendadas = ({
    publicaciones = PUBLICACIONES_EJEMPLO,
    alSeleccionar,
}) => {
    return (
        <VStack gap={3} align="stretch" w="100%">
            <Text fontSize="sm" fontWeight="semibold" color="gray.600">
                Ofertas recomendadas del reciclador
            </Text>
            {publicaciones.map((pub) => (
                <TarjetaPublicacionRecomendada
                    key={pub.id}
                    tipoMaterial={pub.tipoMaterial}
                    iconoMaterial={pub.iconoMaterial}
                    pesoEstimado={pub.pesoEstimado}
                    precioReferencial={pub.precioReferencial}
                    distancia={pub.distancia}
                    puntuacion={pub.puntuacion}
                    alVerDetalle={() => alSeleccionar && alSeleccionar(pub.id)}
                />
            ))}
        </VStack>
    );
};

export default ListaPublicacionesRecomendadas;
