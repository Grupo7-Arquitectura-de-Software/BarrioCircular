import { useState } from "react";
import { Button, FileUpload, Text, VStack, useFileUpload } from "@chakra-ui/react";
import { MdOutlineAddAPhoto, MdOutlineCameraAlt } from "react-icons/md";
import Icono from "../atomos/Icono.jsx";
import CamaraCaptura from "./CamaraCaptura.jsx";

/**
 * Área de carga de imágenes (arrastrar/soltar o explorar archivos). Con
 * `permitirCamara`, ofrece además tomar la foto con la cámara del dispositivo;
 * la captura entra al mismo flujo que un archivo subido (dispara
 * `alCambiarArchivos`).
 */
const AreaCargaImagenes = ({
  maximoArchivos = 3,
  tamanioMaximoMB = 10,
  alCambiarArchivos,
  permitirCamara = false,
}) => {
  const [camaraAbierta, setCamaraAbierta] = useState(false);
  const cargaArchivos = useFileUpload({
    accept: "image/*",
    maxFiles: maximoArchivos,
    maxFileSize: tamanioMaximoMB * 1024 * 1024,
    onFileChange: alCambiarArchivos,
  });

  return (
    <FileUpload.RootProvider value={cargaArchivos}>
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

      {permitirCamara && (
        <>
          <Button
            type="button"
            variant="outline"
            colorPalette="verde"
            rounded="lg"
            w="100%"
            onClick={() => setCamaraAbierta(true)}
          >
            <MdOutlineCameraAlt /> Tomar foto con la cámara
          </Button>
          <CamaraCaptura
            abierto={camaraAbierta}
            alCerrar={() => setCamaraAbierta(false)}
            alCapturar={(archivo) => cargaArchivos.setFiles([archivo])}
          />
        </>
      )}

      <FileUpload.ItemGroup>
        <FileUpload.Context>
          {({ acceptedFiles }) =>
            acceptedFiles.map((archivo) => (
              <FileUpload.Item
                key={archivo.name}
                file={archivo}
                w="100%"
                display="flex"
                alignItems="center"
                gap={3}
                overflow="hidden"
              >
                <FileUpload.ItemPreview flexShrink={0}>
                  <FileUpload.ItemPreviewImage boxSize="60px" objectFit="cover" borderRadius="md" />
                </FileUpload.ItemPreview>
                <FileUpload.ItemName flex="1" minW={0} truncate />
                <FileUpload.ItemSizeText flexShrink={0} />
                <FileUpload.ItemDeleteTrigger flexShrink={0} />
              </FileUpload.Item>
            ))
          }
        </FileUpload.Context>
      </FileUpload.ItemGroup>
    </FileUpload.RootProvider>
  );
};

export default AreaCargaImagenes;
