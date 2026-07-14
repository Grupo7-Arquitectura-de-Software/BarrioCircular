import { IconButton, Link } from "@chakra-ui/react";
import { FaWhatsapp } from "react-icons/fa";

const normalizarTelefonoWa = (telefono) => {
  if (!telefono) return null;
  const limpio = telefono.replace(/\s|-/g, "");
  if (limpio.startsWith("+593")) return limpio.slice(1);
  if (limpio.startsWith("593")) return limpio;
  if (limpio.startsWith("0")) return "593" + limpio.slice(1);
  return limpio;
};

const BotonWhatsApp = ({
  telefono,
  mensaje = "Hola, vi tu publicación en Barrio Circular y me gustaría coordinar el retiro del material.",
  tamanio = "md",
}) => {
  const numeroNormalizado = normalizarTelefonoWa(telefono);
  if (!numeroNormalizado) return null;

  const urlWhatsApp = `https://wa.me/${numeroNormalizado}?text=${encodeURIComponent(mensaje)}`;

  return (
    <IconButton
      as={Link}
      href={urlWhatsApp}
      target="_blank"
      rel="noopener noreferrer"
      aria-label="Contactar por WhatsApp"
      title="Contactar por WhatsApp"
      size={tamanio}
      variant="ghost"
      color="#25D366"
      _hover={{ bg: "green.50", color: "#128C7E" }}
      onClick={(e) => e.stopPropagation()}
    >
      <FaWhatsapp />
    </IconButton>
  );
};

export default BotonWhatsApp;
