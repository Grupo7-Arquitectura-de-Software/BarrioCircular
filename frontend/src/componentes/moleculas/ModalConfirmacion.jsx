import { Button, Dialog, Portal, Text } from "@chakra-ui/react";

/**
 * Modal de confirmación genérico para acciones destructivas o irreversibles
 * (ej. eliminar una publicación).
 */
const ModalConfirmacion = ({
  abierto,
  alCerrar,
  titulo = "¿Estás seguro?",
  mensaje,
  textoConfirmar = "Confirmar",
  confirmando = false,
  alConfirmar,
}) => (
  <Dialog.Root open={abierto} onOpenChange={(detalle) => !detalle.open && alCerrar?.()}>
    <Portal>
      <Dialog.Backdrop />
      <Dialog.Positioner>
        <Dialog.Content>
          <Dialog.Header>
            <Dialog.Title>{titulo}</Dialog.Title>
          </Dialog.Header>
          <Dialog.Body>
            <Text color="gray.600">{mensaje}</Text>
          </Dialog.Body>
          <Dialog.Footer>
            <Button variant="ghost" rounded="lg" onClick={alCerrar} disabled={confirmando}>
              Cancelar
            </Button>
            <Button
              colorPalette="red"
              rounded="lg"
              loading={confirmando}
              loadingText="Eliminando"
              onClick={alConfirmar}
            >
              {textoConfirmar}
            </Button>
          </Dialog.Footer>
        </Dialog.Content>
      </Dialog.Positioner>
    </Portal>
  </Dialog.Root>
);

export default ModalConfirmacion;
