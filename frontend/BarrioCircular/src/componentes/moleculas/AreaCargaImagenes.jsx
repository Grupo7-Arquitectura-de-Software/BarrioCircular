import {FileUpload, Icon, Button} from "@chakra-ui/react";
import {LuUpload} from "react-icons/lu";
import Titulos from "../atomos/Titulos";
import Texto from "../atomos/Texto";

const AreaCargaImagenes = ({
                               etiqueta = "Subir imágenes",
                               maximoArchivos = 5,
                               tamanioMaximoMB = 5,
                               alCambiarArchivos,
                           }) => {
    return (
        <FileUpload.Root
            accept="image/*"
            maxFiles={maximoArchivos}
            maxFileSize={tamanioMaximoMB * 1024 * 1024}
            onFileChange={alCambiarArchivos}
        >
            <FileUpload.HiddenInput/>

            <FileUpload.Label>
                <Titulos Titulo={etiqueta} tamanio="md" volumendeFuente="semibold"/>
            </FileUpload.Label>

            <FileUpload.Dropzone>
                <FileUpload.DropzoneContent>
                    <Icon fontSize="2xl" color="gray.400">
                        <LuUpload/>
                    </Icon>
                    <Texto
                        texto="Arrastra tus imágenes aquí o haz clic para buscarlas"
                        tamanio="sm"
                        volumendeFuente="medium"
                    />
                    <Texto
                        texto={`PNG, JPG o WEBP — máximo ${tamanioMaximoMB}MB por archivo`}
                        tamanio="xs"
                        volumendeFuente="normal"
                    />
                </FileUpload.DropzoneContent>

                <FileUpload.Trigger asChild>
                    <Button variant="outline" size="sm" mt={2}>
                        Seleccionar imágenes
                    </Button>
                </FileUpload.Trigger>
            </FileUpload.Dropzone>

            <FileUpload.ItemGroup>
                <FileUpload.Context>
                    {({acceptedFiles}) =>
                        acceptedFiles.map((archivo) => (
                            <FileUpload.Item key={archivo.name} file={archivo}>
                                <FileUpload.ItemPreview>
                                    <FileUpload.ItemPreviewImage/>
                                </FileUpload.ItemPreview>
                                <FileUpload.ItemName/>
                                <FileUpload.ItemSizeText/>
                                <FileUpload.ItemDeleteTrigger/>
                            </FileUpload.Item>
                        ))
                    }
                </FileUpload.Context>
            </FileUpload.ItemGroup>
        </FileUpload.Root>
    );
};

export default AreaCargaImagenes;