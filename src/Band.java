
public class Band {
    private final int m_ID;
    private String m_name;
    private short m_formYear;
    private short m_disbandYear;

    Band(int id, String name, short formYear, short disbandYear) {
        m_ID = id;
        m_name = name;
        m_formYear = formYear;
        m_disbandYear = disbandYear;
    }

    public int getID() {
        return m_ID;
    }

    public String getName() {
        return m_name;
    }

    public short getFormYear() {
        return m_formYear;
    }

    public Short getDisbandYear() {
        return m_disbandYear != 0 ? m_disbandYear : null;
    }

    public void setName(String name) {
        m_name = name;
    }

    public void setFormYear(short year) {
        m_formYear = year;
    }

    public void setDisbandYear(short year) {
        m_disbandYear = year;
    }

    public String toStringValue() {
        String result = "" + m_ID + ": " + m_name + " (" + m_formYear + "-";
        if (m_disbandYear != 0)
            result += m_disbandYear + ")";
        else result += "настоящее время)";

        return result;
    }
}
