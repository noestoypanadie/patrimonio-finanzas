package es.vdiaz.patrimonio.finanzas.medias;

public class Accion {

	private String nombre;
	private Integer totalAcciones = 0;
	private Float precioActual = 0F;
	private Float precioMedio = 0F;
	private Integer numeroDeCompras = 0;
	private Integer numeroDeVentas = 0;
	private Float dividendosCobrados = 0F;
	private Float comisiones = 0F;

	public Accion(String nombre) {
		this.nombre = nombre;
	}

	public Float getDividendosCobrados() {
		return dividendosCobrados;
	}

	public void addDividendosCobrados(Float dividendos) {
		dividendosCobrados += dividendos;
	}

	public String getNombre() {
		return nombre;
	}

	public Float getDividendoMedioAnual() {
		return Dividendos.getInstance().getDividendo(nombre);
	}

	public void addAcciones(Integer numeroAcciones, Float precioActual) {
		this.precioActual = precioActual;
		this.precioMedio = (precioMedio * totalAcciones + numeroAcciones * precioActual) / (numeroAcciones + totalAcciones);
		this.totalAcciones += numeroAcciones;
		this.numeroDeCompras++;
		this.comisiones += Constantes.COMISION_COMPRA_ACCIONES_NACIONALES;
	}

	public void vendeAcciones(Integer numeroAcciones, Float precioActual) {
		this.precioActual = precioActual;
		this.totalAcciones -= numeroAcciones;
		this.numeroDeVentas++;
		this.comisiones += Constantes.COMISION_VENTA_ACCIONES_NACIONALES;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();

		sb.append(nombre);
		sb.append(" | acciones: ").append(totalAcciones);
		sb.append(" | numero de compras: ").append(getNumeroDeCompras());
		sb.append(" | numero de ventas: ").append(getNumeroDeVentas());
		sb.append(" | invertido: ").append(getInvertido());
		sb.append(" | dividendos cobrados: ").append(getDividendosCobrados());
		sb.append(" | valor: ").append(getValorActual());
		sb.append(" | plusvalias: ").append(getPlusvalias());
		sb.append(" | precio medio: ").append(getPrecioMedio());
		sb.append(" | % ganancias: ").append(getValorActual() / getInvertido());
		sb.append(" | % ganancias por dividendos: ").append(getDividendosCobrados() / getInvertido());
		sb.append("\n");

		return sb.toString();
	}

	public Float getPorcentajeGananciasPorAccion() {
		return precioActual / precioMedio;
	}

	public void setPrecioActual(Float precioActual) {
		this.precioActual = precioActual;
	}

	public Integer getNumeroDeCompras() {
		return numeroDeCompras;
	}

	public Float getPrecioMedio() {
		return precioMedio;
	}

	public Float getPrecioActual() {
		return precioActual;
	}

	public Float getPlusvalias() {
		return totalAcciones * (precioActual - precioMedio);
	}

	public Integer getTotalAcciones() {
		return totalAcciones;
	}

	public Float getValorActual() {
		return totalAcciones * precioActual;
	}

	public Float getInvertido() {
		return totalAcciones * precioMedio;
	}

	public Integer getNumeroDeVentas() {
		return numeroDeVentas;
	}
}
