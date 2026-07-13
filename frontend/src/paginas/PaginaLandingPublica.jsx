import { Box } from "@chakra-ui/react";

import LandingComoFunciona from "@/componentes/organismos/LandingComoFunciona.jsx";
import LandingFooter from "@/componentes/organismos/LandingFooter.jsx";
import LandingHeader from "@/componentes/organismos/LandingHeader.jsx";
import LandingHero from "@/componentes/organismos/LandingHero.jsx";
import LandingSeccionRoles from "@/componentes/organismos/LandingSeccionRoles.jsx";

const PaginaLandingPublica = () => (
  <Box minH="100vh" bg="fondo.pagina" overflowX="hidden">
    <LandingHeader />
    <Box as="main">
      <LandingHero />
      <LandingComoFunciona />
      <LandingSeccionRoles />
      <LandingFooter />
    </Box>
  </Box>
);

export default PaginaLandingPublica;
