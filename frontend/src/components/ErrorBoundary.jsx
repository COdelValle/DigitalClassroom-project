import { Component } from "react";
import { Container, Alert, Button } from "react-bootstrap";

class ErrorBoundary extends Component {
  constructor(props) {
    super(props);
    this.state = {
      hasError: false,
      error: null,
      info: null,
    };
  }

  static getDerivedStateFromError(error) {
    return { hasError: true, error };
  }

  componentDidCatch(error, info) {
    console.error("ErrorBoundary caught an error:", error, info);
    this.setState({ info });
  }

  render() {
    if (this.state.hasError) {
      return (
        <Container className="py-5">
          <Alert variant="danger">
            <Alert.Heading>Error inesperado</Alert.Heading>
            <p>
              Ocurrió un problema al cargar esta sección. Intenta recargar la página o vuelve más tarde.
            </p>
            <hr />
            <p className="mb-0 text-break">
              {this.state.error?.message || "Se produjo un error desconocido."}
            </p>
          </Alert>
          <div className="d-flex justify-content-center">
            <Button variant="primary" onClick={() => window.location.reload()}>
              Recargar
            </Button>
          </div>
        </Container>
      );
    }

    return this.props.children;
  }
}

export default ErrorBoundary;
