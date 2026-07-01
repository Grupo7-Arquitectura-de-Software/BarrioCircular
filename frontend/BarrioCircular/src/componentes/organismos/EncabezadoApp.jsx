import { HStack, Text, Box , Menu, Portal} from "@chakra-ui/react";
import { LuChevronLeft, LuMenu } from "react-icons/lu";

const EncabezadoApp = ({
    titulo = "BarrioCircular",
    mostrarAtras = true,
    alPresionarAtras,
    opcionesMenu= [],
}) => {
    return (
        <HStack
            px={{ base: 3, md: 6 }}
            py={{ base: 2, md: 3 }}
            bg="white"
            borderBottom="1px solid"
            borderColor="gray.200"
            justify="space-between"
            align="center"
            position="sticky"
            top={0}
            zIndex={10}
        >
            {/* Botón atrás o espacio */}
            <Box
                w="32px"
                h="32px"
                display="flex"
                alignItems="center"
                justifyContent="center"
                cursor={mostrarAtras ? "pointer" : "default"}
                onClick={alPresionarAtras}
                color="gray.700"
            >
                {mostrarAtras && <LuChevronLeft size={22} />}
            </Box>

            {/* Título */}
            <Text fontSize={{ base: "md", md: "lg" }} fontWeight="semibold" color="gray.800">
                {titulo}
            </Text>

            <Menu.Root>
                     <Menu.Trigger asChild>
                         <Box
                             w="32px" h="32px" display="flex" alignItems="center"
                             justifyContent="center" cursor="pointer" color="gray.700"
                         >
                             <LuMenu size={22} />
                         </Box>
                     </Menu.Trigger>
                 <Portal>
                     <Menu.Positioner>
                         <Menu.Content>
                             {opcionesMenu.map(({ valor, etiqueta, alSeleccionar }) => (
                                 <Menu.Item key={valor} value={valor} onSelect={alSeleccionar}>
                                         {etiqueta}
                                     </Menu.Item>
                             ))}
                         </Menu.Content>
                     </Menu.Positioner>
                 </Portal>
             </Menu.Root>

        </HStack>
    );
};

export default EncabezadoApp;
