package lang.specs;

import java.util.ArrayList;

import lang.core.ChooseNode;

public class StateDescList extends ChooseNode {

	private ArrayList<StateDesc> list = new ArrayList<StateDesc>();

	public StateDescList() {
	}

	public StateDescList(ArrayList<StateDesc> list) {
		this.list = list;
	}

	public void add(StateDesc sd) {
		list.add(sd);
	}

	public ArrayList<StateDesc> getList() {
		return list;
	}

	public void setList(ArrayList<StateDesc> list) {
		this.list = list;
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();

		for (int i = 0; i < list.size(); i++) {
			b.append(list.get(i));

			if (i < list.size() - 1) {
				b.append(", ");
			}
		}
		return b.toString();
	}

	public StateDescList smartClone() {
		StateDescList sdlist = new StateDescList();
		for (StateDesc sd : list) {
			sdlist.add(sd.smartClone());
		}

		return sdlist;
	}

}
