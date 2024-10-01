package myclasses;

public class Personne {
	
	private String name;
	private int age;
	

	public Personne(String name, int age) {
		this.setName(name);
		this.setAge(age);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}
	public boolean isAdult() {
		return age >= 18;
	}
	@Override
	public String toString() {
		return "Je suis "+this.name+" et j'ai "+this.age;
	}
}
