import { Box, Flex } from "@chakra-ui/react";
import { fondoPagina } from "./estilosLayout";


const DiseniodeAutenticacion = ({ children }) => {
    return (
        <Flex 
            {...fondoPagina} 
            align="center" 
            justify="center" 
            p={{ base: 4, md: 8 }}
            bgGradient="linear(to-br, gray.50, blue.50)"
            position="relative"
            overflow="hidden"
            minH="100vh"
            w="100%"
        >
            {/* Elementos decorativos de fondo fluidos */}
            <Box
                position="absolute"
                top="-15%"
                left="-10%"
                w="50vw"
                h="50vw"
                bg="blue.100"
                borderRadius="full"
                filter="blur(120px)"
                opacity="0.6"
                zIndex="0"
                animation="float 10s ease-in-out infinite"
                sx={{
                    "@keyframes float": {
                        "0%, 100%": { transform: "translateY(0)" },
                        "50%": { transform: "translateY(-20px)" }
                    }
                }}
            />
            <Box
                position="absolute"
                bottom="-15%"
                right="-10%"
                w="40vw"
                h="40vw"
                bg="green.100"
                borderRadius="full"
                filter="blur(100px)"
                opacity="0.5"
                zIndex="0"
                animation="floatReverse 12s ease-in-out infinite"
                sx={{
                    "@keyframes floatReverse": {
                        "0%, 100%": { transform: "translateY(0)" },
                        "50%": { transform: "translateY(20px)" }
                    }
                }}
            />
            
            <Box
                zIndex="1"
                w="100%"
                maxW="480px"
                bg="rgba(255, 255, 255, 0.9)"
                backdropFilter="blur(16px)"
                borderRadius="2xl"
                border="1px solid"
                borderColor="rgba(255, 255, 255, 0.6)"
                boxShadow="2xl"
                p={{ base: 6, md: 10 }}
                transition="all 0.4s cubic-bezier(0.4, 0, 0.2, 1)"
            >
                {children}
            </Box>
        </Flex>
    );
};

export default DiseniodeAutenticacion;
