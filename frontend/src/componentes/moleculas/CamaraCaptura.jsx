import { useEffect, useRef, useState } from "react";
import { Box, Button, Dialog, Portal, Text } from "@chakra-ui/react";
import { MdOutlineCameraAlt } from "react-icons/md";

/**
 * Modal que activa la cámara del dispositivo (getUserMedia) con vista previa en
 * vivo y captura un fotograma como archivo JPG, entregado vía `alCapturar`.
 * Prefiere la cámara trasera en móviles (facingMode "environment"). Requiere
 * contexto seguro (HTTPS o localhost); si el navegador no da acceso, muestra el
 * error y el usuario puede seguir subiendo la foto desde sus archivos.
 */
const CamaraCaptura = ({ abierto, alCerrar, alCapturar }) => {
  const videoRef = useRef(null);
  const streamRef = useRef(null);
  const [error, setError] = useState("");
  const [camaraLista, setCamaraLista] = useState(false);

  useEffect(() => {
    if (!abierto) return undefined;

    let cancelado = false;

    const iniciarCamara = async () => {
      if (!navigator.mediaDevices?.getUserMedia) {
        setError("Tu navegador no permite usar la cámara aquí. Sube la foto desde tus archivos.");
        return;
      }
      try {
        const stream = await navigator.mediaDevices.getUserMedia({
          video: { facingMode: "environment" },
          audio: false,
        });
        if (cancelado) {
          stream.getTracks().forEach((pista) => pista.stop());
          return;
        }
        streamRef.current = stream;
        if (videoRef.current) videoRef.current.srcObject = stream;
        setCamaraLista(true);
      } catch {
        if (!cancelado) {
          setError(
            "No se pudo acceder a la cámara. Revisa los permisos del navegador o sube la foto desde tus archivos.",
          );
        }
      }
    };

    iniciarCamara();

    return () => {
      cancelado = true;
      streamRef.current?.getTracks().forEach((pista) => pista.stop());
      streamRef.current = null;
    };
  }, [abierto]);

  // El estado se limpia al cerrar (no dentro del efecto) para que cada
  // apertura del modal arranque la cámara desde cero.
  const cerrar = () => {
    setError("");
    setCamaraLista(false);
    alCerrar?.();
  };

  const capturarFoto = () => {
    const video = videoRef.current;
    if (!video || !video.videoWidth) return;

    const lienzo = document.createElement("canvas");
    lienzo.width = video.videoWidth;
    lienzo.height = video.videoHeight;
    lienzo.getContext("2d").drawImage(video, 0, 0);
    lienzo.toBlob(
      (blob) => {
        if (!blob) return;
        const archivo = new File([blob], `foto-material-${Date.now()}.jpg`, {
          type: "image/jpeg",
        });
        alCapturar?.(archivo);
        cerrar();
      },
      "image/jpeg",
      0.9,
    );
  };

  return (
    <Dialog.Root open={abierto} onOpenChange={(detalle) => !detalle.open && cerrar()} size="lg">
      <Portal>
        <Dialog.Backdrop />
        <Dialog.Positioner>
          <Dialog.Content>
            <Dialog.Header>
              <Dialog.Title>Tomar foto del material</Dialog.Title>
            </Dialog.Header>
            <Dialog.Body>
              {error ? (
                <Text color="red.600" fontSize="sm">
                  {error}
                </Text>
              ) : (
                <Box borderRadius="lg" overflow="hidden" bg="black">
                  <video
                    ref={videoRef}
                    autoPlay
                    playsInline
                    muted
                    style={{ width: "100%", display: "block" }}
                  />
                </Box>
              )}
            </Dialog.Body>
            <Dialog.Footer>
              <Button variant="ghost" rounded="lg" onClick={cerrar}>
                Cancelar
              </Button>
              {!error && (
                <Button
                  colorPalette="verde"
                  bg="marca.primario"
                  rounded="lg"
                  onClick={capturarFoto}
                  disabled={!camaraLista}
                >
                  <MdOutlineCameraAlt /> Capturar
                </Button>
              )}
            </Dialog.Footer>
          </Dialog.Content>
        </Dialog.Positioner>
      </Portal>
    </Dialog.Root>
  );
};

export default CamaraCaptura;
