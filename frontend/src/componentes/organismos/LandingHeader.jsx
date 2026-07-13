import { useEffect, useState } from "react";
import {
  Box,
  Button,
  CloseButton,
  Drawer,
  Flex,
  HStack,
  IconButton,
  Link,
  Portal,
  VStack,
} from "@chakra-ui/react";
import { useNavigate } from "react-router-dom";
import { MdMenu } from "react-icons/md";

import LogotipoApp from "@/componentes/atomos/LogotipoApp.jsx";

const enlacesNavegacion = [
  { etiqueta: "Cómo funciona", href: "#como-funciona" },
  { etiqueta: "Elige tu rol", href: "#roles" },
];

const LandingHeader = () => {
  const navigate = useNavigate();
  const [menuAbierto, setMenuAbierto] = useState(false);
  const [conSombra, setConSombra] = useState(false);

  useEffect(() => {
    const actualizarSombra = () => setConSombra(window.scrollY > 8);
    actualizarSombra();
    window.addEventListener("scroll", actualizarSombra, { passive: true });
    return () => window.removeEventListener("scroll", actualizarSombra);
  }, []);

  useEffect(() => {
    if (!menuAbierto) return undefined;

    const cerrarConEscape = (evento) => {
      if (evento.key === "Escape") setMenuAbierto(false);
    };

    document.addEventListener("keydown", cerrarConEscape);
    return () => document.removeEventListener("keydown", cerrarConEscape);
  }, [menuAbierto]);

  const navegarARuta = (ruta) => {
    setMenuAbierto(false);
    navigate(ruta);
  };

  const cerrarMenu = () => setMenuAbierto(false);

  const enlaces = (
    <>
      {enlacesNavegacion.map((enlace) => (
        <Link
          key={enlace.href}
          href={enlace.href}
          color="gray.700"
          fontWeight="600"
          fontSize="sm"
          _hover={{ color: "marca.primario", textDecoration: "none" }}
          _focusVisible={{
            outline: "2px solid",
            outlineColor: "marca.primario",
            outlineOffset: "3px",
          }}
          onClick={cerrarMenu}
        >
          {enlace.etiqueta}
        </Link>
      ))}
    </>
  );

  return (
    <Flex
      as="header"
      position="sticky"
      top={0}
      zIndex={20}
      bg="rgba(255, 255, 255, 0.88)"
      backdropFilter="blur(14px)"
      borderBottom="1px solid"
      borderColor={conSombra ? "gray.200" : "transparent"}
      boxShadow={conSombra ? "sm" : "none"}
      px={{ base: 4, md: 8 }}
      py={3}
      align="center"
      justify="center"
    >
      <Flex w="100%" maxW="1180px" align="center" justify="space-between" gap={4}>
        <Link
          href="#inicio"
          _hover={{ textDecoration: "none" }}
          _focusVisible={{
            outline: "2px solid",
            outlineColor: "marca.primario",
            outlineOffset: "3px",
          }}
          onClick={cerrarMenu}
        >
          <LogotipoApp tamanio="sm" orientacion="horizontal" />
        </Link>

        <HStack as="nav" gap={6} display={{ base: "none", lg: "flex" }} ml="auto">
          {enlaces}
        </HStack>

        <HStack gap={3} display={{ base: "none", lg: "flex" }}>
          <Button
            variant="outline"
            colorPalette="verde"
            rounded="lg"
            onClick={() => navigate("/auth")}
          >
            Iniciar sesión
          </Button>
          <Button
            colorPalette="verde"
            bg="marca.primario"
            rounded="lg"
            onClick={() => navigate("/seleccionar-rol")}
          >
            Únete ahora
          </Button>
        </HStack>

        <IconButton
          aria-label="Abrir menú de navegación"
          variant="ghost"
          colorPalette="verde"
          display={{ base: "inline-flex", lg: "none" }}
          aria-expanded={menuAbierto}
          aria-controls="landing-menu-movil"
          onClick={() => setMenuAbierto(true)}
        >
          <MdMenu aria-hidden="true" />
        </IconButton>
      </Flex>

      <Drawer.Root
        open={menuAbierto}
        onOpenChange={(evento) => setMenuAbierto(evento.open)}
        placement="end"
      >
        <Portal>
          <Drawer.Backdrop />
          <Drawer.Positioner>
            <Drawer.Content id="landing-menu-movil" maxW="min(320px, calc(100vw - 24px))">
              <Drawer.Header>
                <LogotipoApp tamanio="sm" orientacion="horizontal" />
              </Drawer.Header>
              <Drawer.Body>
                <VStack align="stretch" gap={5}>
                  <VStack as="nav" align="stretch" gap={4}>
                    {enlaces}
                  </VStack>
                  <Box borderTop="1px solid" borderColor="gray.200" pt={5}>
                    <VStack align="stretch" gap={3}>
                      <Button
                        variant="outline"
                        colorPalette="verde"
                        rounded="lg"
                        onClick={() => navegarARuta("/auth")}
                      >
                        Iniciar sesión
                      </Button>
                      <Button
                        colorPalette="verde"
                        bg="marca.primario"
                        rounded="lg"
                        onClick={() => navegarARuta("/seleccionar-rol")}
                      >
                        Únete ahora
                      </Button>
                    </VStack>
                  </Box>
                </VStack>
              </Drawer.Body>
              <Drawer.CloseTrigger asChild>
                <CloseButton size="sm" position="absolute" top={3} right={3} />
              </Drawer.CloseTrigger>
            </Drawer.Content>
          </Drawer.Positioner>
        </Portal>
      </Drawer.Root>
    </Flex>
  );
};

export default LandingHeader;
