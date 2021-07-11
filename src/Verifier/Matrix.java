package Verifier;

public class Matrix {
	public int[][] matrix;
	private GlobalStateManager gsm;

	public Matrix(int[][] matrix, GlobalStateManager gsm) {
		this.matrix = matrix;
		this.gsm = gsm;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
//		for (int i = 0; i < matrix.length; i++) {
//			for (int j = 0; j < matrix[i].length; j++) {
//				sb.append(matrix[i][j]).append("  ");
//			}
//			sb.append("\n");
//		}
//		sb.append("\n");

		boolean identiity = true;
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[i].length; j++) {

				if (i != j && matrix[i][j] == 1) { // non-trivial move
					identiity = false;
					sb.append(gsm.getStateOf(j).toSmallString() + " to " + gsm.getStateOf(i).toSmallString() + "\n");
				}
			}
		}

		if (identiity) {
			sb.append(" Identity");
		}
		sb.append("\n");
		return sb.toString();
	}

}
