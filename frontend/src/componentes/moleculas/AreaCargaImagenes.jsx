import { FileUpload, Text, VStack } from "@chakra-ui/react";
import { MdOutlineAddAPhoto } from "react-icons/md";
import Icono from "../atomos/Icono.jsx";

const AreaCargaImagenes = ({ maximoArchivos = 3, tamanioMaximoMB = 10, alCambiarArchivos }) => {
  return (
    <FileUpload.Root
      accept="image/*"
      maxFiles={maximoArchivos}
      maxFileSize={tamanioMaximoMB * 1024 * 1024}
      onFileChange={alCambiarArchivos}
    >
      <FileUpload.HiddenInput />

      <FileUpload.Dropzone
        w="100%"
        borderStyle="dashed"
        borderColor="gray.300"
        borderRadius="lg"
        bg="fondo.tarjeta"
        py={10}
        cursor="pointer"
      >
        <VStack gap={2}>
          <Icono componente={<MdOutlineAddAPhoto />} tamanio="3xl" color="gray.400" />
          <Text fontSize="sm">
            <Text as="span" color="marca.primario" fontWeight="600">
              Subir Foto
            </Text>{" "}
            o arrastrar y soltar
          </Text>
          <Text fontSize="xs" color="gray.500">
            PNG, JPG, GIF hasta {tamanioMaximoMB}MB
          </Text>
        </VStack>
      </FileUpload.Dropzone>

      <FileUpload.ItemGroup>
        <FileUpload.Context>
          {({ acceptedFiles }) =>
            acceptedFiles.map((archivo) => (
              <FileUpload.Item key={archivo.name} file={archivo}>
                <FileUpload.ItemPreview>
                  <FileUpload.ItemPreviewImage />
                </FileUpload.ItemPreview>
                <FileUpload.ItemName />
                <FileUpload.ItemSizeText />
                <FileUpload.ItemDeleteTrigger />
              </FileUpload.Item>
            ))
          }
        </FileUpload.Context>
      </FileUpload.ItemGroup>
    </FileUpload.Root>
  );
};

export default AreaCargaImagenes;
