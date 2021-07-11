package synthesis;

public abstract class BackendTranslator<T> {
	public abstract T translate(Constraint constraint);
}
