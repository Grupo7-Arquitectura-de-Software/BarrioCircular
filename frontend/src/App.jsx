import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";

import PaginadeSeleccionRol from "@/paginas/PaginadeSeleccionRol.jsx";
import PaginaAutenticacion from "@/paginas/PaginaAutenticacion.jsx";
import PaginaCompletarPerfil from "@/paginas/PaginaCompletarPerfil.jsx";
import PaginaInicioRecolector from "@/paginas/PaginaInicioRecolector.jsx";
import ValidadorSesion from "@/componentes/proveedores/ValidadorSesion.jsx";

import PaginaPanelCiudadano from "@/paginas/PaginaPanelCiudadano.jsx";
import PaginaConfiguracion from "@/paginas/PaginaConfiguracion.jsx";
import PaginaAyuda from "@/paginas/PaginaAyuda.jsx";
import PaginaCrearPublicaciones from "@/paginas/PaginaCrearPublicaciones.jsx";
import PaginaPublicacionesDisponibles from "@/paginas/PaginaPublicacionesDisponibles.jsx";
import PaginadeColeccionCoordenadas from "@/paginas/PaginadeColeccionCoordenadas.jsx";
import PaginaEntregarMaterial from "@/paginas/PaginaEntregarMaterial.jsx";
import PaginaResultadoOperacion from "@/paginas/PaginaResultadoOperacion.jsx";

import PaginaPublicacionesRecomendadas from "@/paginas/PaginaPublicacionesRecomendadas.jsx";
import PaginaDetallePublicacion from "@/paginas/PaginaDetallePublicacion.jsx";
import PaginaCoordinarRecoleccion from "@/paginas/PaginaCoordinarRecoleccion.jsx";
import PaginadeValidacionMaterial from "@/paginas/PaginadeValidacionMaterial.jsx";
import PaginadeConfirmacionOperacion from "@/paginas/PaginadeConfirmacionOperacion.jsx";
import PaginaResultadoFinalComprador from "@/paginas/PaginaResultadoFinalComprador.jsx";

import PaginaCentroBuscarMateriales from "@/paginas/PaginaCentroBuscarMateriales.jsx";
import PaginaCentroValidacionMaterial from "@/paginas/PaginaCentroValidacionMaterial.jsx";
import PaginaCentroConfirmacionOperacion from "@/paginas/PaginaCentroConfirmacionOperacion.jsx";
import PaginaMiQrIdentidad from "@/paginas/PaginaMiQrIdentidad.jsx";
import PaginaVerificacionIdentidadPublica from "@/paginas/PaginaVerificacionIdentidadPublica.jsx";

const PREFIJO_RECOLECTOR_VENDER = "/recolector/vender";

function App() {
  return (
    <BrowserRouter>
      <ValidadorSesion>
        <Routes>
          <Route path="/" element={<Navigate to="/seleccionar-rol" replace />} />
          <Route path="/seleccionar-rol" element={<PaginadeSeleccionRol />} />
          <Route path="/auth/*" element={<PaginaAutenticacion />} />
          <Route path="/verificar/:token" element={<PaginaVerificacionIdentidadPublica />} />
          <Route path="/completar-perfil" element={<PaginaCompletarPerfil />} />
          <Route path="/ciudadano/panel" element={<PaginaPanelCiudadano />} />
          <Route
            path="/ciudadano/configuracion"
            element={<PaginaConfiguracion rol="ciudadano" />}
          />
          <Route path="/ciudadano/ayuda" element={<PaginaAyuda rol="ciudadano" />} />
          <Route path="/ciudadano/crear-publicacion" element={<PaginaCrearPublicaciones />} />
          <Route
            path="/ciudadano/publicacion-disponible"
            element={<PaginaPublicacionesDisponibles />}
          />
          <Route path="/ciudadano/coordinar" element={<PaginadeColeccionCoordenadas />} />
          <Route path="/ciudadano/entregar-material" element={<PaginaEntregarMaterial />} />
          <Route path="/ciudadano/resultado" element={<PaginaResultadoOperacion />} />

          <Route path="/recolector/inicio" element={<PaginaInicioRecolector />} />
          <Route
            path="/recolector/configuracion"
            element={<PaginaConfiguracion rol="recolector" />}
          />
          <Route path="/recolector/identidad" element={<PaginaMiQrIdentidad />} />
          <Route path="/recolector/ayuda" element={<PaginaAyuda rol="recolector" />} />

          <Route
            path="/recolector/vender/crear-publicacion"
            element={<PaginaCrearPublicaciones prefijoRuta={PREFIJO_RECOLECTOR_VENDER} />}
          />
          <Route
            path="/recolector/vender/publicacion-disponible"
            element={<PaginaPublicacionesDisponibles prefijoRuta={PREFIJO_RECOLECTOR_VENDER} />}
          />
          <Route
            path="/recolector/vender/coordinar"
            element={<PaginadeColeccionCoordenadas prefijoRuta={PREFIJO_RECOLECTOR_VENDER} />}
          />
          <Route
            path="/recolector/vender/entregar-material"
            element={<PaginaEntregarMaterial prefijoRuta={PREFIJO_RECOLECTOR_VENDER} />}
          />
          <Route path="/recolector/vender/resultado" element={<PaginaResultadoOperacion />} />

          <Route
            path="/recolector/publicaciones-recomendadas"
            element={<PaginaPublicacionesRecomendadas rol="recolector" />}
          />
          <Route
            path="/recolector/detalle/:id"
            element={<PaginaDetallePublicacion rol="recolector" />}
          />
          <Route
            path="/recolector/coordinar/:id"
            element={<PaginaCoordinarRecoleccion rol="recolector" />}
          />
          <Route path="/recolector/verificar/:id" element={<PaginadeValidacionMaterial />} />
          <Route
            path="/recolector/confirmar-operacion"
            element={<PaginadeConfirmacionOperacion />}
          />
          <Route
            path="/recolector/resultado"
            element={<PaginaResultadoFinalComprador rol="recolector" />}
          />

          <Route path="/centro/buscar-materiales" element={<PaginaCentroBuscarMateriales />} />
          <Route path="/centro/configuracion" element={<PaginaConfiguracion rol="centro" />} />
          <Route path="/centro/identidad" element={<PaginaMiQrIdentidad />} />
          <Route path="/centro/ayuda" element={<PaginaAyuda rol="centro" />} />
          <Route
            path="/centro/publicaciones-recomendadas"
            element={<PaginaPublicacionesRecomendadas rol="centro" />}
          />
          <Route path="/centro/detalle/:id" element={<PaginaDetallePublicacion rol="centro" />} />
          <Route
            path="/centro/coordinar/:id"
            element={<PaginaCoordinarRecoleccion rol="centro" />}
          />
          <Route path="/centro/verificar/:id" element={<PaginaCentroValidacionMaterial />} />
          <Route
            path="/centro/confirmar-operacion"
            element={<PaginaCentroConfirmacionOperacion />}
          />
          <Route
            path="/centro/resultado"
            element={<PaginaResultadoFinalComprador rol="centro" />}
          />

          <Route path="*" element={<Navigate to="/seleccionar-rol" replace />} />
        </Routes>
      </ValidadorSesion>
    </BrowserRouter>
  );
}

export default App;
