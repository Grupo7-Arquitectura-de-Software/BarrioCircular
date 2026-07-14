import { Heading } from "@chakra-ui/react";

const Titulos = ({ Titulo, volumendeFuente, tamanio }) => {
  return (
    <Heading size={tamanio} fontWeight={volumendeFuente}>
      {Titulo}
    </Heading>
  );
};

export default Titulos;
