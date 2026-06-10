/**
 * Página de Gestión de Evaluaciones
 * Acceso: URL /assessments
 */

import { Navigate } from "react-router-dom";
import AssessmentsList from "../components/AssessmentsList";

export default function Assessment() {
  const userRole = localStorage.getItem("userRole") || "";

  if (userRole !== "profesor") {
    return <Navigate to="/clase" replace />;
  }

  return (
    <div>
      <AssessmentsList />
    </div>
  );
}
